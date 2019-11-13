package edu.harvard.iq.policymodels.model.policyspace.values;

import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;

/**
 * A placeholder value, to be filled in later in the
 * development of the questionnaire. Still, needs to be represented
 * in this system for completeness. {@code TODO} types don't have real values
 * anyway, since if they had, they would not have been on the TO-DO list.
 * 
 * @author michael
 */
public class ToDoValue extends AbstractValue {
    private final String info;

	public ToDoValue(ToDoSlot type, String someInfo) {
		super(type);
        info = someInfo;
	}

    @Override
    public ToDoSlot getSlot() {
        return (ToDoSlot) super.getSlot();
    }
    
    @Override
    public CompareResult compare(AbstractValue otherValue) {
        if ( otherValue == null ) throw new IllegalArgumentException("Cannot compare a value to null");
        if ( equals(otherValue) ) return CompareResult.Same;
        if ( ! otherValue.getSlot().equals(getSlot()) ) return CompareResult.Incomparable;
        if ( ! (otherValue instanceof ToDoValue) )  return CompareResult.Incomparable;
        
        return CompareResult.Same; // ToDo slots have a single value only. If we're here, it's the same slot, so same value.
    }
    
	@Override
	public <R> R accept(edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue.Visitor<R> tv) {
		return tv.visitToDoValue(this);
	}

    public String getInfo() {
        return info;
    }
	
    @Override
    protected String contentToString() {
        return "<TODO>";
    }
}
