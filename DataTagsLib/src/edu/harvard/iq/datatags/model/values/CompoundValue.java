package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;

/**
 * A value that has multiple fields of different types.
 *
 * @author michael
 */
public class CompoundValue extends TagValue {

    private static final Resolver RESOLVER = new Resolver();

    private final Map<TagType, TagValue> fields = new HashMap<>();

    public CompoundValue(CompoundType type) {
        super(type);
    }

    @Override
    public CompoundType getType() {
        return (CompoundType) super.getType();
    }

    public void set(TagValue value) {
        if (getType().getFieldTypes().contains(value.getType())) {
            fields.put(value.getType(), value);
        } else {
            throw new IllegalArgumentException("Type " + getType() + " does not have a field of type " + value.getType() + ".");
        }
    }

    public void clear(TagType type) {
        fields.remove(type);
    }


    public TagValue get(TagType type) {
        if (getType().getFieldTypes().contains(type)) {
            return fields.get(type);
        } else {
            throw new IllegalArgumentException("Type " + getType() + " does not have a field of type " + type + ".");
        }
    }

    /**
     * All the field types for which {@code this} has values.
     *
     * @return Set of all the types who have non-null values.
     */
    public Set<TagType> getTypesWithNonNullValues() {
        return fields.keySet();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCompoundValue(this);
    }

    @Override
    public CompoundValue getOwnableInstance() {
        return buildOwnableInstance(getType().createInstance());
    }

    protected <T extends CompoundValue> T buildOwnableInstance(T startingPoint) {
        for (TagType tt : getTypesWithNonNullValues()) {
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
        if (!(obj instanceof CompoundValue)) {
            return false;
        }
        final CompoundValue other = (CompoundValue) obj;
        return super.equals(obj) && Objects.equals(this.fields, other.fields);
    }

    @Override
    protected String tagValueToString() {
        StringBuilder sb = new StringBuilder();
        fields.values().stream()
                .forEach(tv -> sb.append(tv.toString()));
        return "<" + sb + ">";
    }

    /**
     * Returns a copy with values of both {@code this} and {@code other}. For
     * each value type T, the composition is as follows:
     * <ul>
     * <li>If only one tag contains a value for T, that value is used in the
     * result tags</li>
     * <li>Otherwise:
     * <ul><li>If the value is a simple value, the stricter value (i.e. higher
     * ordinal) is chosen for the result tag.</li>
     * <li>If the value is an aggregate value, the result tag will contain the
     * union of the two values</li>
     * <li>If the value is a compound value, the result will be calculated
     * recursively. </li>
     * </ul>
     * </li>
     * </ul>
     *
     * Note: if {@code other} is {@code null}, this method behaves as
     * {@link #getOwnableInstance()}.
     *
     * @param other
     * @return A new DataTags object, composed from {@code this} and
     * {@code other}.
     */
    public CompoundValue composeWith(CompoundValue other) {
        if (other == null) {
            return getOwnableInstance();
        }
        if (!getType().equals(other.getType())) {
            throw new RuntimeException("Cannot compose values of different types (" + getType() + " and " + other.getType() + ")");
        }

        CompoundValue result = getType().createInstance();
        // Composing. Note that for each type in types, at least one object has a non-null value
        for (TagType tp : C.unionSet(getTypesWithNonNullValues(), other.getTypesWithNonNullValues())) {
            TagValue ours = get(tp);
            TagValue its = other.get(tp);
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
 /**
     * Checks whether:
     * <ul>
     * <li>{@code this} instance agrees on all the values defined in {@code other}, and</li>
     * <li>{@code other} has no fields missing from {@code this}.</li>
     * </ul>
     * @param other
     * @return {@code true} iff {@code this} is a superset of {@code other}, as defined above.
     */
    public boolean isSupersetOf( CompoundValue other ) {
        if ( !(getTypesWithNonNullValues().containsAll(other.getTypesWithNonNullValues())) ) {
            // condition 2 unsatisfied - other has more defined fields than this
            return false;
        }
        
        for( TagType type : getTypesWithNonNullValues() ){
            TagValue ourValue = get( type );
            TagValue otherValue = other.get(type);
            if ( otherValue != null ) {
                if ( ! ourValue.accept(new SubsetComparator()).test(otherValue) ) {
                    return false;
                }
            }
        }
        return true;
    }

}


class Resolver implements TagValue.Visitor<TagValue.Function> {

    @Override
    public TagValue.Function visitAtomicValue(final AtomicValue op1) {
        return (TagValue v) -> {
            if (v == null) {
                return op1.getOwnableInstance();
            }
            AtomicValue op2 = (AtomicValue) v;
            return (op1.compareTo(op2) > 0 ? op1 : op2).getOwnableInstance();
        };
    }

    @Override
    public TagValue.Function visitAggregateValue(final AggregateValue op1) {
        return new TagValue.Function() {
            @Override
            public TagValue apply(TagValue v) {
                AggregateValue res = op1.getOwnableInstance();
                if (v == null) {
                    return res;
                }
                AggregateValue op2 = (AggregateValue) v;
                for (AtomicValue tv : op2.getValues()) {
                    res.add(tv);
                }
                return res;
            }
        };
    }

    @Override
    public TagValue.Function visitToDoValue(ToDoValue v) {
        return (TagValue v1) -> v1;
    }

    @Override
    public TagValue.Function visitCompoundValue(final CompoundValue cv) {
        return new TagValue.Function() {
            @Override
            public TagValue apply(TagValue v) {
                CompoundValue res = cv.getOwnableInstance();
                if (v == null) {
                    return res;
                }
                CompoundValue cv2 = (CompoundValue) v;
                for (TagType tt : C.unionSet(cv2.getTypesWithNonNullValues(), cv.getTypesWithNonNullValues())) {
                    res.set(
                            (res.get(tt) == null) ? cv2.get(tt)
                            : ((TagValue.Function) cv.get(tt).accept(Resolver.this)).apply(cv2.get(tt)));
                }
                return res;
            }
        };
    }
}

class SubsetComparator implements TagValue.Visitor<Predicate<TagValue>> {

    @Override
    public Predicate<TagValue> visitToDoValue(ToDoValue thisValue) {
        return (TagValue t) -> thisValue.equals(t);
    }

    @Override
    public Predicate<TagValue> visitAtomicValue(AtomicValue thisValue) {
        return (TagValue t) -> thisValue.equals(t);
    }

    @Override
    public Predicate<TagValue> visitAggregateValue(AggregateValue thisValue) {
        return (TagValue other)->thisValue.getValues().containsAll( ((AggregateValue)other).getValues() );
    }

    @Override
    public Predicate<TagValue> visitCompoundValue(CompoundValue thisValue) {
        return (TagValue other)->thisValue.isSupersetOf((CompoundValue)other);
    }

}