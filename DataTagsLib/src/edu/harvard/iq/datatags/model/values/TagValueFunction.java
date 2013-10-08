package edu.harvard.iq.datatags.model.values;

/**
 * A base class for functions that take a single tag value and return a tag value.
 * @author michael
 */
public interface TagValueFunction {
	public TagValue apply( TagValue v );
}
