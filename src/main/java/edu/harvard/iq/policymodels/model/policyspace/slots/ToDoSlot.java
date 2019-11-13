package edu.harvard.iq.policymodels.model.policyspace.slots;

import edu.harvard.iq.policymodels.model.policyspace.values.ToDoValue;

/**
 * A placeholder tag.
 * @author michael
 */
public class ToDoSlot extends AbstractSlot {
    
    final ToDoValue singleValue = new ToDoValue(this, null);
    
	public ToDoSlot(String name, String info) {
		super(name, info);
	}
    
    public ToDoValue getValue() {
        return singleValue;
    }
    
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitTodoSlot(this);
	}
	
}
