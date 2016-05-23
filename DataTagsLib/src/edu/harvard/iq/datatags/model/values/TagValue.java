package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.SlotType;
import java.util.Objects;

/**
 * Base class for a value / instance of a given {@link SlotType}.
 * @author michael
 */
public abstract class TagValue {
	
	public interface Visitor<R> {
		R visitToDoValue(ToDoValue v);
		R visitAtomicValue(AtomicValue v);
		R visitAggregateValue(AggregateValue v);
		R visitCompoundValue(CompoundValue aThis);
	}

	public interface Function {
        TagValue apply(TagValue v);
	}

	private final SlotType type;

    public TagValue(SlotType type) {
		this.type = type;
	}


	public SlotType getType() {
		return type;
	}

	
	public abstract <R> R accept( TagValue.Visitor<R> visitor );
	
	/**
	 * Returns an instance that can take part in private copies of value 
	 * collections. In simple values, where all the data is immutable anyway,
	 * it just returns {@code this}. In aggregate values, where state is mutable,
	 * a new instance, created by deep-copying the state, is returned.
	 * 
	 * @return An instance that can be safely stored.
	 */
	public TagValue getOwnableInstance() {
		return this;
	}
    
	@Override
	public int hashCode() {
		int hash = 37 * getType().hashCode();
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
		if ( !(obj instanceof TagValue) ) {
			return false;
		}
		final TagValue other = (TagValue) obj;
	
        return Objects.equals(getType(), other.getType());
	}

	@Override
	public String toString() {
		return "[TagValue type:" + type + " value:" + tagValueToString() + "]";
	}
	
    protected abstract String tagValueToString();
}
