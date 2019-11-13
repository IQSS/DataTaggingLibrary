package edu.harvard.iq.policymodels.tools;

import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot.VoidVisitor;
import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
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
