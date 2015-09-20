package edu.harvard.iq.datatags.parser.definitions;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.definitions.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.definitions.ast.CompoundSlot;
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
        // TODO implement
        return Optional.empty();
    }
    
}
