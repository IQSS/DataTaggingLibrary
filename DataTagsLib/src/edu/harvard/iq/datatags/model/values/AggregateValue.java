package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A tag value that can contain a varying amount of items.
 * Value equality is based on type and contained values - <em>not</em> on the name.
 * This is the same as in programming languages, where the name of the variable
 * is not part of the equality test.
 * 
 * @author michael
 */
public class AggregateValue extends AbstractValue {
	
	private final Set<AtomicValue> values = new HashSet<>();

    public AggregateValue(AggregateSlot type) {
		super(type);
	}
	
    /**
     * Returns an unmodifiable list of the values in {@code this} value.
     * @return members of this aggregation.
     */
	public Set<AtomicValue> getValues() {
		return Collections.unmodifiableSet(values);
	}
	
	public void add( Collection<? extends AtomicValue> valueCollection ) {
        valueCollection.stream().forEach( this::add );
	}
	
    @Override
    public AggregateSlot getSlot() {
        return (AggregateSlot) super.getSlot();
    }
    
    @Override
    public CompareResult compare(AbstractValue otherValue) {
        if ( otherValue == null ) throw new IllegalArgumentException("Cannot compare a value to null");
        if ( equals(otherValue) ) return CompareResult.Same;
        if ( ! otherValue.getSlot().equals(getSlot()) ) return CompareResult.Incomparable;
        if ( ! (otherValue instanceof AggregateValue) )  return CompareResult.Incomparable;
        
        AggregateValue other = (AggregateValue) otherValue;
        boolean containsOther = getValues().containsAll(other.getValues());
        boolean containedInOther = other.getValues().containsAll( getValues() );
        
        if ( containsOther && containedInOther ) return CompareResult.Same;
        if ( containsOther && !containedInOther ) return CompareResult.Bigger;
        if ( !containsOther && containedInOther ) return CompareResult.Smaller;
        return CompareResult.Incomparable;
    }
    
	public void add( AtomicValue tagValue ) {
		if ( ! tagValue.getSlot().equals(getSlot().getItemType()) ) {
			throw new IllegalArgumentException( "Added value type ("+tagValue.getSlot()+") "
					+ "different from aggregated type (" + getSlot().getItemType() + ")");
					
		}
		values.add( tagValue );
	}
	
	@Override
	public <R> R  accept(edu.harvard.iq.datatags.model.values.AbstractValue.Visitor<R> tv) {
		return tv.visitAggregateValue(this);
	}
	
	@Override
	public AggregateValue getOwnableInstance() {
		AggregateValue copy = new AggregateValue(getSlot());
		for ( AtomicValue v : values ) {
			copy.add( v );
		}
		return copy;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 43 * hash + Objects.hashCode(this.values);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof AggregateValue)) {
			return false;
		}
		final AggregateValue other = (AggregateValue) obj;
		return super.equals(obj) &&
				Objects.equals(this.values, other.values);
	}
	
	@Override
    protected String contentToString() {
        return "<" + getValues() + ">";
    }

}
