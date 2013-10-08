package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.AggregateType;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A tag value that can contain a varying amount of items.
 * @author michael
 */
public class AggregateValue extends TagValue<AggregateType>{
	
	private final Set<TagValue> values = new HashSet<>();

	public AggregateValue(String name, AggregateType type, String info) {
		super(name, type, info);
	}
	
	public Set<TagValue> getValues() {
		return Collections.unmodifiableSet(values);
	}
	
	public void addValue( TagValue tagValue ) {
		values.add( tagValue );
	}
	
	@Override
	public <R> R  accept(TagValueVisitor<R> tv) {
		return tv.visitAggregateValue(this);
	}
	
	@Override
	public AggregateValue getOwnableInstance() {
		AggregateValue copy = new AggregateValue(getName(), getType(), getInfo());
		for ( TagValue v : values ) {
			copy.addValue( v.getOwnableInstance() );
		}
		return copy;
	}
	
}
