package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.parser.tagspace.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.AggregateSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.AtomicSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompoundSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.ToDoSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import java.util.Collection;
import java.util.HashMap;
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
    
    private final List<? extends AbstractSlot> slots; 
    private final Map<String, AbstractSlot> slotsByName = new TreeMap<>();
    private Set<String> duplicateSlotNames;
    
    class MissingSlotException extends RuntimeException {
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
    
    TagSpaceParseResult( List<? extends AbstractSlot> someSlots ) throws SemanticsErrorException {
        slots = someSlots;
        
        Map<String, List<AbstractSlot>> slotMap = slots.stream().collect(Collectors.groupingBy(AbstractSlot::getName));
            
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
    
    public List<? extends AbstractSlot> getSlots() {
        return slots;
    }
    
    public Optional<AbstractSlot> getSlot( String name ) {
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
     * @throws edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException if the slot is of the wrong type, or there are duplicate slot names..
     */
    public Optional<CompoundType> buildType( String slotName ) throws SemanticsErrorException  {
        
        if ( duplicateSlotNames != null ) {
                throw new SemanticsErrorException( new CompilationUnitLocationReference(-1, -1), 
                    "The following slots were defined more than once: " + duplicateSlotNames );
        }
        
        AbstractSlot slot = slotsByName.get( slotName );
        if ( slot == null ) {
            return Optional.empty();
        }
        if ( ! (slot instanceof CompoundSlot) ) {
            throw new SemanticsErrorException(null, "Slot " + slotName + " is not a compound (consists of) slot");
        }
        
        CompoundSlot baseSlot = (CompoundSlot) slot;
        TypeBuilder tb = new TypeBuilder();
        
        try {
            return Optional.of( (CompoundType)baseSlot.accept(tb) );
            
        } catch ( MissingSlotException mse ) {
            throw new SemanticsErrorException( new CompilationUnitLocationReference(-1, -1), 
                    "Slot " + mse.getDefiningSlot() + " references nonexistent slot named " + mse.getMissingSlotName() );
        }
    }
    
    /**
     * Visits a slot, builds a type based on it.
     */
    class TypeBuilder implements AbstractSlot.Visitor<TagType> {

        @Override
        public TagType visit(ToDoSlot slot) {
            return new ToDoType(slot.getName(), slot.getNote());
        }

        @Override
        public TagType visit(AtomicSlot slot) {
            AtomicType newType = new AtomicType(slot.getName(), slot.getNote());
            slot.getValueDefinitions().forEach( vd -> newType.registerValue(vd.getName(), vd.getNote()) );
            
            return newType;
        }

        @Override
        public TagType visit(AggregateSlot slot) {
            AtomicType itemType = new AtomicType( slot.getName() + "#item", "" );
            AggregateType newType = new AggregateType(slot.getName(), slot.getNote(), itemType );
            
            slot.getValueDefinitions().forEach( vd -> itemType.registerValue(vd.getName(), vd.getNote()) );
            
            return newType;
        }

        @Override
        public CompoundType visit(CompoundSlot slot) {
            CompoundType newType = new CompoundType(slot.getName(), slot.getNote());
            slot.getSubSlotNames().forEach( 
                (String name) -> 
                    newType.addFieldType( 
                            Optional.ofNullable(slotsByName.get(name))
                                .orElseThrow( ()->new MissingSlotException(name, slot.getName()) )
                                .accept(TypeBuilder.this)));
                    
            return newType;
        }
    }
}
