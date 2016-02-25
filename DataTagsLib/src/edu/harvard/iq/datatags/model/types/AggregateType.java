package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;

/**
 * A type whose values contain varying number of (sub-) values, of type {@link #itemType}.
 * @author michael
 */
public class AggregateType extends TagType {
	
	private final AtomicType itemType;
	
	public AggregateType(String name, String info, AtomicType itemType) {
		super(name, info);
		this.itemType = itemType;
	}

	public AtomicType getItemType() {
		return itemType;
	}
	
    public AggregateValue createInstance() {
        return new AggregateValue(this);
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitAggregateType(this);
	}
	
}
