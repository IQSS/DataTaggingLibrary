package edu.harvard.iq.datatags.model.types;

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
	
}
