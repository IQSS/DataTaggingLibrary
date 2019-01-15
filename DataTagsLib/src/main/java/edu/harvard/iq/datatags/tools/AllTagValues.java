package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot.VoidVisitor;
import edu.harvard.iq.datatags.model.slots.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import java.util.HashSet;
import java.util.Set;

/**
 * Traverse all TagTypes and gather all
 * existing TagValues.
 * 
 * @author Naomi
 */
public class AllTagValues extends VoidVisitor {
    private final Set<AbstractValue> definedTagValues = new HashSet<>();
    
    public Set<AbstractValue> gatherAllTagValues( CompoundSlot cs ) {
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
        t.getSubSlots().forEach(tagType -> tagType.accept(this));
    }

    @Override
    public void visitTodoSlotImpl(ToDoSlot t) {
        definedTagValues.add(t.getValue());
    }
    
}
