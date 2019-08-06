package edu.harvard.iq.policymodels.util;

import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import edu.harvard.iq.policymodels.model.policyspace.values.AggregateValue;
import edu.harvard.iq.policymodels.model.policyspace.values.AtomicValue;
import edu.harvard.iq.policymodels.model.policyspace.values.CompoundValue;
import edu.harvard.iq.policymodels.model.policyspace.values.ToDoValue;

/**
 * A class for storing static utility methods that work with policy space
 * items.
 * 
 * @author michael
 */
public final class PolicySpaceHelper {
   
    private PolicySpaceHelper(){}
    
    private static final AbstractValue.Visitor<String> VALUE_NAMER = new AbstractValue.Visitor<String>(){
        @Override
        public String visitToDoValue(ToDoValue v) {
            return "todo";
        }

        @Override
        public String visitAtomicValue(AtomicValue v) {
            return v.getName();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            return "Set of " + v.getSlot().getItemType().getName();
        }

        @Override
        public String visitCompoundValue(CompoundValue v) {
            return null;
        }
    };
    
    private static final AbstractValue.Visitor<String> VALUE_NOTE_RETRIEVER = new AbstractValue.Visitor<String>(){
        @Override
        public String visitToDoValue(ToDoValue v) {
            return null;
        }

        @Override
        public String visitAtomicValue(AtomicValue v) {
            return v.getNote();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            return null;
        }

        @Override
        public String visitCompoundValue(CompoundValue v) {
            return null;
        }
    };
    
    public static String name(AbstractValue val){
        return val.accept(VALUE_NAMER);
    }
    
    public static String note(AbstractValue val){
        return val.accept(VALUE_NOTE_RETRIEVER);
    }
    
}
