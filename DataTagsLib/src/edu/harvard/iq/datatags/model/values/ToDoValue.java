package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.ToDoType;

/**
 * A placeholder value, to be filled in later in the
 * development of the questionnaire. Still, needs to be represented
 * in this system for completeness. {@code TODO} types don't have real values
 * anyway, since if they had, they would not have been on the TO-DO list.
 * 
 * @author michael
 */
public class ToDoValue extends TagValue {
    private final String info;

	public ToDoValue(String name, ToDoType type, String someInfo) {
		super(type);
        info = someInfo;
	}

    @Override
    public ToDoType getType() {
        return (ToDoType) super.getType();
    }
    
	@Override
	public <R> R accept(edu.harvard.iq.datatags.model.values.TagValue.Visitor<R> tv) {
		return tv.visitToDoValue(this);
	}

    public String getInfo() {
        return info;
    }
	
    @Override
    protected String tagValueToString() {
        return "<TODO>";
    }
}
