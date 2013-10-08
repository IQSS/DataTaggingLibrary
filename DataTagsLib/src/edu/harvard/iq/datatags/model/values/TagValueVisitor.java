package edu.harvard.iq.datatags.model.values;

/**
 * Visitor pattern for dealing with different tag value classes.
 * @author michael
 */
public interface TagValueVisitor<R> {
	public R visitSimpleValue( SimpleValue v );
	public R visitAggregateValue( AggregateValue v );
	public R visitToDoValue( ToDoValue v );
}
