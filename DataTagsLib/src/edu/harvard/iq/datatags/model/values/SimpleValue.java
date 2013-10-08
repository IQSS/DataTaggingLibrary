package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.SimpleValueType;

/**
 *
 * @author michael
 */
public class SimpleValue extends TagValue<SimpleValueType> implements Comparable<SimpleValue>{
	
	private final int ordinal;

	public SimpleValue(int ordinal, String name, SimpleValueType type, String info) {
		super(name, type, info);
		this.ordinal = ordinal;
	}
	
	@Override
	public int compareTo(SimpleValue o) {
		return getOrdinal() - o.getOrdinal();
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	@Override
	public <R> R accept(TagValueVisitor<R> tv) {
		return tv.visitSimpleValue(this);
	}
	
	
}
