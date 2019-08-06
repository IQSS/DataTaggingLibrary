package edu.harvard.iq.policymodels.model.policyspace.values;

import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import java.util.Objects;

/**
 * Base class for a value / instance of a given {@link AbstractSlot}.
 * @author michael
 */
public abstract class AbstractValue {
	
	public interface Visitor<R> {
		R visitToDoValue(ToDoValue v);
		R visitAtomicValue(AtomicValue v);
		R visitAggregateValue(AggregateValue v);
		R visitCompoundValue(CompoundValue v);
	}

    // TODO: later: can we use the Java function class?
	public interface Function {
        AbstractValue apply(AbstractValue v);
	}
    
    public enum CompareResult {
        Smaller(-1, false),
        Same(0, true),
        Incomparable(0, false),
        Bigger(1, true);
        
        /** Adapter value to Java's {@link Comparable} */
        public final int compareValue;
        
        /** is greater than or equals */
        public final boolean isGtE;
        
        CompareResult( int aCompareValue, boolean gte ) {
            compareValue = aCompareValue;
            isGtE = gte;
        }
    }
    
    /**
     * The slot {@code this} belongs in.
     */
	private final AbstractSlot slot;

    public AbstractValue(AbstractSlot type) {
		this.slot = type;
	}


	public AbstractSlot getSlot() {
		return slot;
	}

	public abstract <R> R accept( AbstractValue.Visitor<R> visitor );
    
    public abstract CompareResult compare( AbstractValue otherValue );

	/**
	 * Returns an instance that can take part in private copies of value 
	 * collections. In simple values, where all the data is immutable anyway,
	 * it just returns {@code this}. In aggregate values, where state is mutable,
	 * a new instance, created by deep-copying the state, is returned.
	 * 
	 * @return An instance that can be safely stored.
	 */
	public AbstractValue getOwnableInstance() {
		return this;
	}
    
	@Override
	public int hashCode() {
		int hash = 37 * getSlot().hashCode();
		return hash;
	}
	
	/**
	 * Base equality test - the type only.
	 * @param obj the other object
	 * @return can these objects be considered equal.
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( !(obj instanceof AbstractValue) ) {
			return false;
		}
		final AbstractValue other = (AbstractValue) obj;
	
        return Objects.equals(getSlot(), other.getSlot());
	}

	@Override
	public String toString() {
		return "[Value slot:" + slot + " content:" + contentToString() + "]";
	}
	
    protected abstract String contentToString();
}
