package edu.harvard.iq.policymodels.parser.policyspace;

import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;
import edu.harvard.iq.policymodels.parser.policyspace.ast.AbstractAstSlot;
import edu.harvard.iq.policymodels.parser.policyspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.policymodels.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.policymodels.parser.policyspace.ast.AggregateAstSlot;
import edu.harvard.iq.policymodels.parser.policyspace.ast.AtomicAstSlot;
import edu.harvard.iq.policymodels.parser.policyspace.ast.CompoundAstSlot;
import edu.harvard.iq.policymodels.parser.policyspace.ast.ToDoAstSlot;
import java.net.URI;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The result of parsing a Tag Space file. While the main target of this class
 * is to build the actual types defined in the file (as the parse result generates an AST, not types),
 * it can also be used for low-level language inspections.
 * 
 * @author michael
 */
public class TagSpaceParseResult {
    
    private final List<? extends AbstractAstSlot> slots; 
    private final Map<String, AbstractAstSlot> slotsByName = new TreeMap<>();
    private Set<String> duplicateSlotNames;
    private URI source;
    
    static class MissingSlotException extends RuntimeException {
        private final String missingSlotName;
        private final String definingSlot;
        MissingSlotException( String aMissingSlotName, String aDefiningSlot ) {
            missingSlotName = aMissingSlotName;
            definingSlot = aDefiningSlot;
        }

        public String getMissingSlotName() {
            return missingSlotName;
        }

        public String getDefiningSlot() {
            return definingSlot;
        }
        
    }
    
    TagSpaceParseResult( List<? extends AbstractAstSlot> someSlots ) throws SemanticsErrorException {
        slots = someSlots;
        
        Map<String, List<AbstractAstSlot>> slotMap = new HashMap<>();
        slots.forEach( s -> slotMap.computeIfAbsent(s.getName(), n -> new LinkedList<>()).add(s));
            
            // Validate that the there are no duplicate slot names
            Set<String> duplicates = slotMap.values().stream()
                    .filter( l -> l.size()>1 )
                    .map( l -> l.get(0).getName() )
                    .collect(Collectors.toSet());
            
            if ( duplicates.isEmpty() ) {
                slotMap.values().stream()
                        .map( l -> l.get(0) )
                        .forEach( s -> slotsByName.put( s.getName(), s) );
            } else {
                duplicateSlotNames = duplicates;
            }
    }
    
    public List<? extends AbstractAstSlot> getSlots() {
        return slots;
    }
    
    public Optional<AbstractAstSlot> getSlot( String name ) {
        return Optional.ofNullable( slotsByName.get(name) );
    }
    
    public Set<String> getSlotNames() {
        return slotsByName.keySet();
    }
    
    
    /**
     * Builds a type from the slot named {@code slotName}. Creating the resultant type will
     * create also all the sub-types references from the slot.
     * @param slotName the name of the slot we build the type from. Has to be a {@link CompoundSlot}.
     * @return A compound type instance based on the slot, or the empty Optional.
     * @throws edu.harvard.iq.policymodels.parser.exceptions.SemanticsErrorException if the slot is of the wrong type, or there are duplicate slot names..
     */
    public Optional<CompoundSlot> buildType( String slotName ) throws SemanticsErrorException  {
        
        if ( duplicateSlotNames != null ) {
                throw new SemanticsErrorException( new CompilationUnitLocationReference(-1, -1), 
                    "The following slots were defined more than once: " + duplicateSlotNames );
        }
        
        AbstractAstSlot slot = slotsByName.get( slotName );
        if ( slot == null ) {
            return Optional.empty();
        }
        if ( ! (slot instanceof CompoundAstSlot) ) {
            throw new SemanticsErrorException(null, "Slot " + slotName + " is not a compound (consists of) slot");
        }
        
        CompoundAstSlot baseSlot = (CompoundAstSlot) slot;
        TypeBuilder tb = new TypeBuilder();
        
        try {
            return Optional.of((CompoundSlot)baseSlot.accept(tb) );
            
        } catch ( MissingSlotException mse ) {
            throw new SemanticsErrorException( new CompilationUnitLocationReference(-1, -1), 
                    "Slot " + mse.getDefiningSlot() + " references nonexistent slot named " + mse.getMissingSlotName() );
        }
    }
    
    /**
     * Visits an slot, builds a SlotType based on it.
     */
    class TypeBuilder implements AbstractAstSlot.Visitor<AbstractSlot> {

        @Override
        public AbstractSlot visit(ToDoAstSlot slot) {
            return new ToDoSlot(slot.getName(), slot.getNote());
        }

        @Override
        public AbstractSlot visit(AtomicAstSlot slot) {
            AtomicSlot newType = new AtomicSlot(slot.getName(), slot.getNote());
            slot.getValueDefinitions().forEach( vd -> newType.registerValue(vd.getName(), vd.getNote()) );
            
            return newType;
        }

        @Override
        public AbstractSlot visit(AggregateAstSlot slot) {
            AtomicSlot itemType = new AtomicSlot( slot.getName() + "#item", "" );
            AggregateSlot newType = new AggregateSlot(slot.getName(), slot.getNote(), itemType );
            itemType.setParentSlot(newType);
            
            slot.getValueDefinitions().forEach( vd -> itemType.registerValue(vd.getName(), vd.getNote()) );
            
            return newType;
        }

        @Override
        public CompoundSlot visit(CompoundAstSlot slot) {
            CompoundSlot newType = new CompoundSlot(slot.getName(), slot.getNote());
            slot.getSubSlotNames().forEach( 
                (String name) -> 
                    newType.addSubSlot( 
                            Optional.ofNullable(slotsByName.get(name))
                                .orElseThrow( ()->new MissingSlotException(name, slot.getName()) )
                                .accept(TypeBuilder.this)));
                    
            return newType;
        }
    }

    public URI getSource() {
        return source;
    }

    public void setSource(URI source) {
        this.source = source;
    }
    
}
