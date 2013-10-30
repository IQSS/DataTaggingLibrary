package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;

/**
 * A type whose values contain varying number of (sub-) values.
 * @author michael
 */
public class AggregateType extends TagType {
	
	private final SimpleType itemType;
	
	public AggregateType(String name, String info, SimpleType itemType) {
		super(name, info);
		this.itemType = itemType;
	}

	public SimpleType getItemType() {
		return itemType;
	}
	
	@Override
	public AggregateValue make( String name, String info ) {
		return new AggregateValue(name, this, info);
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitAggregateType(this);
	}
	
}
