package edu.harvard.iq.policymodels.model.policyspace.slots;

import edu.harvard.iq.policymodels.model.policyspace.values.AtomicValue;
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
public class AtomicSlot extends AbstractSlot {
	
	private int nextOrdinal = 0;
    
	private final Map<String, AtomicValue> values = new HashMap<>(); 
    
    private AggregateSlot parentSlot;

	public AtomicSlot(String name, String note) {
        this(name, note, null);
    }
    
	public AtomicSlot(String name, String note, AggregateSlot aParentSlot ) {
		super(name, note);
        parentSlot = aParentSlot;
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
    
    /**
     * Returns the value of {@code this} type that has the passed name,
     * @param name the name of the requested value. Case-sensitive.
     * @return The value with the passed name.
     * @throws IllegalArgumentException if no value by that name exists.
     */
    public AtomicValue valueOf( String name ) {
        if ( values.containsKey(name) ) {
            return values.get(name);
        } else {
            throw new IllegalArgumentException("Atomic type " + getName() + " has no value '" + name + "'");
        }
    }
    
    /**
     * When an {@code this} serves as the item type of an {@link AggregateSlot},
     * that {@link AggregateSlot} is considered the parent slot.
     * @return the parent slot, or {@code null}.
     */
    public AggregateSlot getParentSlot() {
        return parentSlot;
    }

    public void setParentSlot(AggregateSlot parentSlot) {
        this.parentSlot = parentSlot;
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitSimpleSlot(this);
	}
    
    
}
