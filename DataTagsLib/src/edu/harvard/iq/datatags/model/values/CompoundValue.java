package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author michael
 */
public class CompoundValue extends TagValue<CompoundType> {
    
    private static final Resolver RESOLVER = new Resolver();

	private final Map<TagType, TagValue> fields = new HashMap<>();
		
	public CompoundValue(String name, CompoundType type, String info) {
		super(name, type, info);
	}
	
	public void set( TagValue value ) {
		if ( getType().getFieldTypes().contains(value.getType()) ) {
			fields.put(value.getType(), value);
		} else {
			throw new IllegalArgumentException( "Type " + getType() + " does not have a field of type " + value.getType() + ".");
		}
	}
	
	public void clear( TagType type ) {
		fields.remove(type);
	}

	public <T extends TagType> TagValue<T> get( T type ) {
		if ( getType().getFieldTypes().contains(type) ) {
			return fields.get(type);
		} else {
			throw new IllegalArgumentException( "Type " + getType() + " does not have a field of type " + type + ".");
		}
	}
	
	public Set<TagType> getSetFieldTypes() {
		return fields.keySet();
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitCompoundValue( this );
	}
	
	@Override
	public CompoundValue getOwnableInstance() {
		return buildOwnableInstance( new CompoundValue(getName(), getType(), getInfo()) );
	}
	
	protected <T extends CompoundValue> T buildOwnableInstance( T startingPoint ) {
		for ( TagType tt : getSetFieldTypes() ) {
			startingPoint.set(get(tt).getOwnableInstance());
		}
		
		return startingPoint;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 19 * hash + Objects.hashCode(this.fields);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( !(obj instanceof CompoundValue) ) {
			return false;
		}
		final CompoundValue other = (CompoundValue) obj;
		return super.equals(obj) && Objects.equals(this.fields, other.fields);
	}

    /**
     * Returns a copy with values of both {@code this} and {@code other}.
     * For each value type T, the composition is as follows:
     * <ul>
     *	<li>If only one tag contains a value for T, that value is used in the
     *		result tags</li>
     *  <li>Otherwise:
     *		<ul><li>If the value is a simple value, the stricter value (i.e. higher ordinal) is chosen
     *			    for the result tag.</li>
     *			<li>If the value is an aggregate value, the result tag will contain
     *			    the union of the two values</li>
     *			<li>If the value is a compound value, the result will be calculated
     *				recursively. </li>
     *	    </ul>
     *  </li>
     * </ul>
     *
     * Note: if {@code other} is {@code null}, this method behaves as {@link #makeCopy()}.
     * @param other
     * @return A new DataTags object, composed from {@code this} and {@code other}.
     */
    public CompoundValue composeWith(CompoundValue other) {
        if (other == null) {
            return getOwnableInstance();
        }
        if ( ! getType().equals(other.getType()) ) {
            throw new RuntimeException("Cannot compose values of different types (" + getType() + " and " + other.getType() + ")");
        }
        
        CompoundValue result = getType().createInstance();
        // Composing. Note that for each type in types, at least one object has a non-null value
        for (TagType tp : C.unionSet(getSetFieldTypes(), other.getSetFieldTypes())) {
            TagValue<?> ours = get(tp);
            TagValue<?> its = other.get(tp);
            if (ours == null) {
                if (its == null) {
                    throw new IllegalStateException("Both [this] and [other] had null tag value for a tag type");
                } else {
                    result.set(its);
                }
            } else if (its == null) {
                result.set(ours);
            } else {
                result.set(ours.accept(RESOLVER).apply(its));
            }
        }
        return result;
    }
	
	
}

class Resolver implements TagValue.Visitor<TagValue.Function> {

	@Override
	public TagValue.Function visitSimpleValue( final SimpleValue op1 ) {
		return new TagValue.Function(){
			@Override public TagValue apply(TagValue v) {
				if ( v==null ) return op1.getOwnableInstance();
				SimpleValue op2 = (SimpleValue) v;
				return ( op1.compareTo(op2) > 0 ? op1 : op2).getOwnableInstance();
		}};
	}

	@Override
	public TagValue.Function visitAggregateValue( final AggregateValue op1 ) {
		return new TagValue.Function(){
			@Override public TagValue apply(TagValue v) {
				AggregateValue res = op1.getOwnableInstance();
				if ( v==null ) return res;
				AggregateValue op2 = (AggregateValue) v;
				for ( SimpleValue tv : op2.getValues() ) {
					res.add(tv);
				}
				return res;
		}};
	}

	@Override
	public TagValue.Function visitToDoValue( ToDoValue v ) {
		return new TagValue.Function() {
			@Override public TagValue apply(TagValue v) {
				return v;
		}};
	}

	@Override
	public TagValue.Function visitCompoundValue( final CompoundValue cv ) {
		return new TagValue.Function() {
			@Override public TagValue apply(TagValue v) {
				CompoundValue res = cv.getOwnableInstance();
				if ( v==null ) return res;
				CompoundValue cv2 = (CompoundValue) v;
				for ( TagType tt : C.unionSet(cv2.getSetFieldTypes(), cv.getSetFieldTypes())) {
					res.set(
							(res.get(tt)==null) ? cv2.get(tt)
									: ((TagValue.Function)cv.get(tt).accept(Resolver.this)).apply(cv2.get(tt)));
				}
				return res;
		}};
	}
}
