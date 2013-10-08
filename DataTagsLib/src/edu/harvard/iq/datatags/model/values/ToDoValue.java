package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.ToDoType;

/**
 * A placeholder value, to be filled in later in the
 * development of the ChartSet. Still, needs to be represented
 * in this system.
 * 
 * @author michael
 */
public class ToDoValue extends TagValue<ToDoType> {

	public ToDoValue(String name, ToDoType type, String info) {
		super(name, type, info);
	}

	@Override
	public <R> R accept(TagValueVisitor<R> tv) {
		return tv.visitToDoValue(this);
	}
	
}
