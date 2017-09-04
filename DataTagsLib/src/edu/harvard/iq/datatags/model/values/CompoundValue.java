package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import static java.util.stream.Collectors.toSet;

/**
 * A value that has multiple fields of different types.
 *
 * @author michael
 */
public class CompoundValue extends AbstractValue{

    private static final Resolver RESOLVER = new Resolver();

    private final Map<AbstractSlot, AbstractValue> fields = new HashMap<>();

    public CompoundValue(CompoundSlot type) {
        super(type);
    }

    @Override
    public CompoundSlot getSlot() {
        return (CompoundSlot) super.getSlot();
    }

    public void put(AbstractValue value) {
        if (getSlot().getSubSlots().contains(value.getSlot())) {
            fields.put(value.getSlot(), value);
        } else {
            throw new IllegalArgumentException("Slot " + getSlot() + " does not have a sub-slot " + value.getSlot() + ".");
        }
    }

    public void clear(AbstractSlot slot) {
        fields.remove(slot);
    }

    public AbstractValue get(AbstractSlot slot) {
        if (getSlot().getSubSlots().contains(slot)) {
            return fields.get(slot);
        } else {
            throw new IllegalArgumentException("Slot " + getSlot() + " does not have a sub-slot " + slot + ".");
        }
    }

    /**
     * @return All the types of all the sub-slot of {@code this}, where there are values.
     */
    public Set<AbstractSlot> getNonEmptySubSlots() {
        return fields.keySet();
    }

    @Override
    public <R> R accept(Visitor<R> visitor) {
        return visitor.visitCompoundValue(this);
    }

    @Override
    public CompoundValue getOwnableInstance() {
        return buildOwnableInstance(getSlot().createInstance());
    }

    protected <T extends CompoundValue> T buildOwnableInstance(T startingPoint) {
        for (AbstractSlot tt : getNonEmptySubSlots()) {
            startingPoint.put(get(tt).getOwnableInstance());
        }

        return startingPoint;
    }

    @Override
    protected String contentToString() {
        StringBuilder sb = new StringBuilder();
        fields.values().stream()
                .forEach(tv -> sb.append(tv.toString()));
        return "<" + sb + ">";
    }

    /**
     * Checks whether:
     * <ul>
     * <li>{@code this} instance agrees on all the values defined in
     * {@code other}, and</li>
     * <li>{@code other} has no fields missing from {@code this}.</li>
     * </ul>
     *
     * @param other
     * @return {@code true} iff {@code this} is a superset of {@code other}, as
     * defined above.
     */
    public boolean isSupersetOf(CompoundValue other) {
        if (!(getNonEmptySubSlots().containsAll(other.getNonEmptySubSlots()))) {
            // condition 2 unsatisfied - other has more defined fields than this
            return false;
        }

        for (AbstractSlot type : getNonEmptySubSlots()) {
            AbstractValue ourValue = get(type);
            AbstractValue otherValue = other.get(type);
            if (otherValue != null) {
                if (!ourValue.accept(new SubsetComparator()).test(otherValue)) {
                    return false;
                }
            }
        }
        return true;
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
     * @return A new CompoundValue object, composed from {@code this} and
     * {@code other}.
     */
    public CompoundValue composeWith(CompoundValue other) {
        if (other == null) {
            return getOwnableInstance();
        }
        if (!getSlot().equals(other.getSlot())) {
            throw new RuntimeException("Cannot compose values of different types (" + getSlot() + " and " + other.getSlot() + ")");
        }

        CompoundValue result = getSlot().createInstance();
        // Composing. Note that for each type in types, at least one object has a non-null value
        for (AbstractSlot tp : C.unionSet(getNonEmptySubSlots(), other.getNonEmptySubSlots())) {
            AbstractValue ours = get(tp);
            AbstractValue its = other.get(tp);
            if (ours == null) {
                if (its == null) {
                    throw new IllegalStateException("Both [this] and [other] had null tag value for a tag type");
                } else {
                    result.put(its);
                }
            } else if (its == null) {
                result.put(ours);
            } else {
                result.put(ours.accept(RESOLVER).apply(its));
            }
        }
        return result;
    }

    /**
     * Returns a copy with only values shared by {@code this} and {@code other}.
     * For each value type T, the intersection will occur <b>only</b> if both
     * values are equal.
     *
     * Note: if {@code other} is {@code null}, this method behaves as
     * {@link #getOwnableInstance()}.
     *
     * @param other
     * @return A new DataTags object, composed from {@code this} and
     * {@code other}.
     */
    public CompoundValue intersectWith(CompoundValue other) {
        int count = 0;
        if (other == null) {
            return getOwnableInstance();
        }
        if (!getSlot().equals(other.getSlot())) {
            throw new RuntimeException("Cannot compose values of different types (" + getSlot() + " and " + other.getSlot() + ")");
        }

        CompoundValue result = getSlot().createInstance();

        // Composing. Note that for each type in types, at least one object has a non-null value
        for (AbstractSlot tp : C.intersectSet(getNonEmptySubSlots(), other.getNonEmptySubSlots())) {
            AbstractValue ours = get(tp);
            AbstractValue its = other.get(tp);

            /* if both tags were found */
            if ((ours != null) && (its != null)) {
                if (ours.equals(its)) {
                    result.put(ours.getOwnableInstance());
                    count++;
                }
            }
        }
        if (count == 0) {
            return null;
        }
        return result;
    }
    
    /**
     * Project {@code this} over the slots in {@code slots}. In effect, returns
     * a copy of {@code this}, where the set of filled slots is an intersection
     * of the non-empty slots on {@code this}, and {@code slots}.
     *
     * This is somewhat similar to the column list part of a SELECT clause in SQL.
     * 
     * @param slots a set of slots to project {@code this} on.
     * @return A new CompoundValue, with values from {@code this} and slots 
     *         from {@code slots}.
     */
    public CompoundValue project(Set<AbstractSlot> slots) {
        CompoundValue result = getSlot().createInstance();
        C.intersectSet(getNonEmptySubSlots(), slots).stream()
                .map( slot -> get(slot) )
                .forEachOrdered(value -> result.put(value.getOwnableInstance()) );
        return result;
    }
    
    @Override
    public CompareResult compare(AbstractValue otherValue) {
        if ( otherValue == null ) throw new IllegalArgumentException("Cannot compare a value to null");
        if ( equals(otherValue) ) return CompareResult.Same;
        if ( ! otherValue.getSlot().equals(getSlot()) ) return CompareResult.Incomparable;
        if ( ! (otherValue instanceof CompoundValue) ) return CompareResult.Incomparable;
        
        CompoundValue other = (CompoundValue) otherValue;
        if ( ! getNonEmptySubSlots().equals(other.getNonEmptySubSlots()) ) return CompareResult.Incomparable;
        
        Set<CompareResult> results =  getNonEmptySubSlots().stream()
                .map( this::get )
                .map( value -> value.compare(other.get(value.getSlot())) )
                .collect( toSet() );
        
        if ( results.size()==1 ) return results.iterator().next();
        if ( results.contains(CompareResult.Incomparable) ) return CompareResult.Incomparable;
        boolean hasBigger = results.contains(CompareResult.Bigger);
        boolean hasSmaller = results.contains(CompareResult.Smaller);
        
        if (hasBigger && hasSmaller) return CompareResult.Incomparable;
        return hasBigger ? CompareResult.Bigger : CompareResult.Smaller;
        
    }
    
    public Optional<Boolean> isBigger(CompoundValue other) {
        List<AbstractValue> thisValues = new ArrayList<>();
        List<AbstractValue> otherValues = new ArrayList<>();
        getNonEmptySubSlots().forEach(slot -> thisValues.add(get(slot)));
        other.getNonEmptySubSlots().forEach(slot -> otherValues.add(other.get(slot)));
        
        Predicate<AbstractValue> predicate = new Predicate<AbstractValue>() {
            @Override 
            public boolean test(AbstractValue v) {        
            int vPosition = thisValues.indexOf(v);
            if ( v instanceof AtomicValue ) {
                AtomicValue thisValue = (AtomicValue) v;
                AtomicValue otherValue = (AtomicValue) otherValues.get(vPosition);
                return (thisValue.compareTo(otherValue) >= 0);
            } else {
                AggregateValue thisValue = (AggregateValue) v;
                AggregateValue otherValue = (AggregateValue) otherValues.get(vPosition);
                return (thisValue.getValues().containsAll(otherValue.getValues()));
            }}
        };

        if ( thisValues.stream().allMatch(predicate)          ) return Optional.of(Boolean.TRUE);
        if ( thisValues.stream().allMatch(predicate.negate()) ) return Optional.of(Boolean.FALSE);
        
        return Optional.empty();
    }
    
    /**
     * 
     * @return (@code true} iff no slots are set.
     */
    public boolean isEmpty() {
        return getNonEmptySubSlots().isEmpty();
    }

    /**
     * Returns a copy with only values types that {@code this} had and
     * {@code other} does not.
     *
     * Note: if {@code other} is {@code null}, this method behaves as
     * {@link #getOwnableInstance()}.
     *
     * @param other
     * @return A new DataTags object, composed from {@code this} and
     * {@code other}.
     */
    public CompoundValue subtractKeys(CompoundValue other) {
        if (other == null) {
            return this;
        }

        if (!getSlot().equals(other.getSlot())) {
            throw new RuntimeException("Cannot substract values of different types (" + getSlot() + " and " + other.getSlot() + ")");
        }

        CompoundValue result = getSlot().createInstance();

        Set<AbstractSlot> substractedSet = C.subtractSet(getNonEmptySubSlots(), other.getNonEmptySubSlots());

        /* Check if any key left */
        if (substractedSet.isEmpty()) {
            return null;
        }

        // Composing. Note that for each type in types, at least one object has a non-null value
        for (AbstractSlot tp : substractedSet) {
            AbstractValue ours = get(tp);
            result.put(ours.getOwnableInstance());
        }

        return result;
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

}

class Resolver implements AbstractValue.Visitor<AbstractValue.Function> {

    @Override
    public AbstractValue.Function visitAtomicValue(final AtomicValue op1) {
        return (AbstractValue v) -> {
            if (v == null) {
                return op1.getOwnableInstance();
            }
            AtomicValue op2 = (AtomicValue) v;
            return (op1.compareTo(op2) > 0 ? op1 : op2).getOwnableInstance();
        };
    }

    @Override
    public AbstractValue.Function visitAggregateValue(final AggregateValue op1) {
        return (AbstractValue v) -> {
            AggregateValue res = op1.getOwnableInstance();
            if (v == null) {
                return res;
            }
            AggregateValue op2 = (AggregateValue) v;
            op2.getValues().forEach( res::add );
            return res;
        };
    }

    @Override
    public AbstractValue.Function visitToDoValue(ToDoValue v) {
        return (AbstractValue v1) -> v1;
    }

    @Override
    public AbstractValue.Function visitCompoundValue(final CompoundValue cv) {
        return (AbstractValue v) -> {
            CompoundValue res = cv.getOwnableInstance();
            if (v == null) {
                return res;
            }
            CompoundValue cv2 = (CompoundValue) v;
            C.unionSet(cv2.getNonEmptySubSlots(), cv.getNonEmptySubSlots()).stream().forEach((tt) -> {
                res.put((res.get(tt) == null) ? cv2.get(tt)
                        : ((AbstractValue.Function) cv.get(tt).accept(Resolver.this)).apply(cv2.get(tt)));
            });
            return res;
        };
    }
}

class SubsetComparator implements AbstractValue.Visitor<Predicate<AbstractValue>> {

    @Override
    public Predicate<AbstractValue> visitToDoValue(ToDoValue thisValue) {
        return (AbstractValue t) -> thisValue.equals(t);
    }

    @Override
    public Predicate<AbstractValue> visitAtomicValue(AtomicValue thisValue) {
        return (AbstractValue t) -> thisValue.equals(t);
    }

    @Override
    public Predicate<AbstractValue> visitAggregateValue(AggregateValue thisValue) {
        return (AbstractValue other) -> thisValue.getValues().containsAll(((AggregateValue) other).getValues());
    }

    @Override
    public Predicate<AbstractValue> visitCompoundValue(CompoundValue thisValue) {
        return (AbstractValue other) -> thisValue.isSupersetOf((CompoundValue) other);
    }
    
    
}