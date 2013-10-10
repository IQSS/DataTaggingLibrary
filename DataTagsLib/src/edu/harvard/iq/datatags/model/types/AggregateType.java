package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;

/**
 * A type whose values contain varying number of (sub-) values.
 * @author michael
 */
public class AggregateType extends TagType {
	
	private final TagType itemType;
	
	public AggregateType(String name, String info, TagType itemType) {
		super(name, info);
		this.itemType = itemType;
	}

	public TagType getItemType() {
		return itemType;
	}
	
	@Override
	public AggregateValue make( String name, String info ) {
		return new AggregateValue(name, this, info);
	}
	
}
