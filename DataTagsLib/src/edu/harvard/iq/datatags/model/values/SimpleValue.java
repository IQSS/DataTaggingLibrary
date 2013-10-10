package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.SimpleValueType;

/**
 *
 * @author michael
 */
public class SimpleValue extends TagValue<SimpleValueType> implements Comparable<SimpleValue>{
	
	private final int ordinal;

	/**
	 * Creates a new simple value - Note that the preferred way of creating a 
	 * value is by using one of the {@ code type.make()} methods.
	 * @param ordinal ordinal of the value
	 * @param name name of the value
	 * @param type the type of the value
	 * @param info additional info
	 */
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
	public <R> R accept(Visitor<R> tv) {
		return tv.visitSimpleValue(this);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + this.ordinal;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof SimpleValue) ) {
			return false;
		}
		final SimpleValue other = (SimpleValue) obj;
		return super.equals(obj) && getOrdinal() == other.getOrdinal();
	}
	
	
}
