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

	public AtomicType(String name, String info) {
		super(name, info);
	}

	public SortedSet<AtomicValue> values() {
		return Collections.unmodifiableSortedSet(new TreeSet<>(values.values()));
	}
	
	/**
	 * Creates a new value of this type. Maintains all the bookkeeping,
	 * such as ordinality and value aggregation.<br>
     * Values are cached, so subsequent calls with the same name yield the same object.
     * In these cases, the info parameter is ignored.
	 * @param name name of the new value.
	 * @param info additional info (if applicable)
	 * @return A new value of this type.
	 */
	public AtomicValue make( String name, String info ) {
        if ( values.containsKey(name) ) {
            return values.get(name);
        }
		AtomicValue v = new AtomicValue( nextOrdinal++, name, this, info );
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
