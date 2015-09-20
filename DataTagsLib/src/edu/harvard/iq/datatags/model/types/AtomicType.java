package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
/**
 * A type whose values cannot be divided. Values of this type are not
 * mutable, and can thus be shared safely. Values maintain ordering thanks to the 
 * "ordinal" field they have. That field is final.
 * 
 * @author michael
 */
public class AtomicType extends TagType {
	
	private int nextOrdinal = 0;
	
	private final Map<String, AtomicValue> values = new HashMap<>(); 

	public AtomicType(String name, String note) {
		super(name, note);
	}

	public SortedSet<AtomicValue> values() {
		return Collections.unmodifiableSortedSet(new TreeSet<>(values.values()));
	}
	
	/**
	 * Creates a new value of this type. Maintains all the bookkeeping,
	 * such as ordinality and value aggregation.<br>
     * Values are cached, so subsequent calls with the same name yield the same object.
     * 
	 * @param name name of the new value.
	 * @param note additional info (if applicable)
	 * @return A new value of this type.
     * @throws IllegalArgumentException if a value by this name is already registered.
     * @see #valueOf(java.lang.String) 
	 */
	public AtomicValue registerValue( String name, String note ) {
        if ( values.containsKey(name) ) {
            throw new IllegalArgumentException("Value " + name + " already regiseterd.");
        }
		AtomicValue v = new AtomicValue( nextOrdinal++, name, this, note );
		values.put( name, v );
		return v;
	}
    
    public AtomicValue valueOf( String name ) {
        return values.get(name);
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitSimpleType(this);
	}
	
	
}
