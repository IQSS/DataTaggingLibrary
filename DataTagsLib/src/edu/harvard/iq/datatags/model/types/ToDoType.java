package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.ToDoValue;

/**
 * A placeholder tag.
 * @author michael
 */
public class ToDoType extends TagType {
    
    final ToDoValue singleValue = new ToDoValue("todo", this, null);
    
	public ToDoType(String name, String info) {
		super(name, info);
	}
    
    public ToDoValue getValue() {
        return singleValue;
    }
    
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitTodoType(this);
	}
	
}
