package edu.harvard.iq.datatags.model.types;

import java.util.Collections;
import java.util.SortedSet;
import java.util.TreeSet;
import edu.harvard.iq.datatags.model.values.*;
/**
 * A type whose values are defined 
 * @author michael
 */
public class SimpleValueType extends TagType {
	
	SortedSet<SimpleValue> values = new TreeSet<>(); 

	public SimpleValueType(String name, String info) {
		super(name, info);
	}

	public SortedSet<SimpleValue> getValues() {
		return Collections.unmodifiableSortedSet(values);
	}
	
	public void addValue( SimpleValue aValue ) {
		aValue.setType( this );
		values.add( aValue );
	}
	
	
	
}
