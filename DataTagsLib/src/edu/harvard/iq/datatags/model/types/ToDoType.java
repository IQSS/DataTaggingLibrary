package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.ToDoValue;

/**
 * A special tag type for tags under development.
 * @author michael
 */
public class ToDoType extends TagType {

	public ToDoType(String name, String info) {
		super(name, info);
	}
	
	@Override
	public ToDoValue make( String name, String info ) {
		return new ToDoValue(name, this, info);
	}
	
}
