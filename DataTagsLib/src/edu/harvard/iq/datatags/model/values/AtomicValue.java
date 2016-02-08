package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.AtomicType;

/**
 * A value of an {@link AtomicType}.
 * @author michael
 */
public class AtomicValue extends TagValue implements Comparable<AtomicValue>{
	
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
	public AtomicValue(int ordinal, String aName, AtomicType type, String someInfo) {
		super(type);
		this.ordinal = ordinal;
        name = aName;
        note = someInfo;
	}
	
    @Override
    public AtomicType getType() {
        return (AtomicType) super.getType();
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
    protected String tagValueToString() {
        return "<" + getOrdinal() + " " + getName() + ">";
    }
	
	
}
