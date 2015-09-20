package edu.harvard.iq.datatags.parser.definitions;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.parser.definitions.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.definitions.ast.AggregateSlot;
import edu.harvard.iq.datatags.parser.definitions.ast.AtomicSlot;
import edu.harvard.iq.datatags.parser.definitions.ast.CompoundSlot;
import edu.harvard.iq.datatags.parser.definitions.ast.ToDoSlot;
import edu.harvard.iq.datatags.parser.definitions.references.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * The result of parsing a Tag Space file. While the main target of this class
 * is to build the actual types defined in the file (as the parse result generates an AST, not types),
 * it can also be used for low-level language inspections, such as duplicate value names.
 * 
 * @author michael
 */
public class TagSpaceParseResult {
    
    private final Map<String, ? extends AbstractSlot> slotsByName;
    
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
    
    TagSpaceParseResult( Map<String, ? extends AbstractSlot> slots ) {
        slotsByName = slots;
    }
    
    public Collection<? extends AbstractSlot> getSlots() {
        return slotsByName.values();
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
     * @throws edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException if the slot is of the wrong type.
     */
    public Optional<CompoundType> buildType( String slotName ) throws SemanticsErrorException  {
        
        AbstractSlot slot = slotsByName.get( slotName );
        if ( slot == null ) return Optional.empty();
        if ( ! (slot instanceof CompoundSlot) ) throw new SemanticsErrorException(null, "Slot " + slotName + " is not a compound (consists of) slot");
        
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
