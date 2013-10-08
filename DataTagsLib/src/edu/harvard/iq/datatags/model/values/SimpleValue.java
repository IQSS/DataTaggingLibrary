package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.SimpleValueType;

/**
 *
 * @author michael
 */
public class SimpleValue extends TagValue<SimpleValueType> implements Comparable<SimpleValue>{
	
	private int ordinal;
	
	
	@Override
	public int compareTo(SimpleValue o) {
		return getOrdinal() - o.getOrdinal();
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	
}
