package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.slots.AtomicSlot;

/**
 * A value of an {@link AtomicSlot}.
 * @author michael
 */
public class AtomicValue extends AbstractValue implements Comparable<AtomicValue>{
	
	private final int ordinal;
    private final String name;
    private final String note;

	/**
	 * Creates a new simple value - Note that the preferred way of creating a 
	 * value is by using one of the {@code type.make()} methods.
	 * @param ordinal ordinal of the value
	 * @param aName name of the value
	 * @param type the type of the value
	 * @param someInfo additional info
	 */
	public AtomicValue(int ordinal, String aName, AtomicSlot type, String someInfo) {
		super(type);
		this.ordinal = ordinal;
        name = aName;
        note = someInfo;
	}
	
    @Override
    public AtomicSlot getSlot() {
        return (AtomicSlot) super.getSlot();
    }
    
    @Override
    public CompareResult compare(AbstractValue otherValue) {
        if ( otherValue == null ) throw new IllegalArgumentException("Cannot compare a value to null");
        if ( equals(otherValue) ) return CompareResult.Same;
        if ( ! otherValue.getSlot().equals(getSlot()) ) return CompareResult.Incomparable;
        if ( ! (otherValue instanceof AtomicValue) )  return CompareResult.Incomparable;
        
        int cr = compareTo((AtomicValue) otherValue);
        if ( cr<0  ) return CompareResult.Smaller;
        if ( cr==0 ) return CompareResult.Same;
        return CompareResult.Bigger;
    }
    
	@Override
	public int compareTo(AtomicValue o) {
		return getOrdinal() - o.getOrdinal();
	}

	public int getOrdinal() {
		return ordinal;
	}
	
	@Override
	public <R> R accept(Visitor<R> tv) {
		return tv.visitAtomicValue(this);
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + this.ordinal;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof AtomicValue) ) {
			return false;
		}
		final AtomicValue other = (AtomicValue) obj;
		return super.equals(obj) && getOrdinal() == other.getOrdinal();
	}

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }

    @Override
    protected String contentToString() {
        return "<" + getOrdinal() + " " + getName() + ">";
    }
	
	
}
