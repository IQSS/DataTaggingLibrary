package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.AggregateValue;

/**
 * A type whose values contain varying number of (sub-) values, of type {@link #itemType}.
 * @author michael
 */
public class AggregateSlot extends AbstractSlot {
	
	private final AtomicSlot itemType;
	
	public AggregateSlot(String name, String info, AtomicSlot itemType) {
		super(name, info);
		this.itemType = itemType;
	}

	public AtomicSlot getItemType() {
		return itemType;
	}
	
    public AggregateValue createInstance() {
        return new AggregateValue(this);
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitAggregateSlot(this);
	}
	
}
