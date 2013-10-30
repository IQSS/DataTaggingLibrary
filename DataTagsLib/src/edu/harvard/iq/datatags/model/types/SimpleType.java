package edu.harvard.iq.datatags.model.types;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import edu.harvard.iq.datatags.model.values.*;
/**
 * A type whose values cannot be divided. Values of this type are not
 * mutable, and can thus be shared safely. Values maintain ordering thanks to the 
 * "ordinal" field they have. That field is final.
 * 
 * @author michael
 */
public class SimpleType extends TagType {
	
	private int nextOrdinal = 0;
	
	SortedSet<SimpleValue> values = new TreeSet<>(); 

	public SimpleType(String name, String info) {
		super(name, info);
	}

	public SortedSet<SimpleValue> values() {
		return Collections.unmodifiableSortedSet(values);
	}
	
	public void addValue( SimpleValue aValue ) {
		values.add( aValue );
	}
	
	/**
	 * Creates a new value of this type. Maintains all the bookkeeping,
	 * such as ordinality and value aggregation.
	 * @param name name of the new value.
	 * @param info additional info (if applicable)
	 * @return A new value of this type.
	 */
	@Override
	public SimpleValue make( String name, String info ) {
		SimpleValue v = new SimpleValue( nextOrdinal++, name, this, info );
		values.add( v );
		return v;
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitSimpleType(this);
	}
	
	
}
