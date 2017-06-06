package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.SlotType.VoidVisitor;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Traverse all TagTypes and gather all
 * existing TagValues.
 * 
 * @author Naomi
 */
public class AllTagValues extends VoidVisitor {
    private final Set<TagValue> definedTagValues = new HashSet<>();
    
    public Set<TagValue> gatherAllTagValues( CompoundSlot cs ) {
        cs.accept(this);
        return definedTagValues;
    }

    @Override
    public void visitAtomicSlotImpl(AtomicSlot t) {
        t.values().forEach( definedTagValues::add );
    }

    @Override
    public void visitAggregateSlotImpl(AggregateSlot t) {
        t.getItemType().accept(this);
    }

    @Override
    public void visitCompoundSlotImpl(CompoundSlot t) {
        t.getFieldTypes().forEach(tagType -> tagType.accept(this));
    }

    @Override
    public void visitTodoSlotImpl(ToDoSlot t) {
        definedTagValues.add(t.getValue());
    }
    
}
