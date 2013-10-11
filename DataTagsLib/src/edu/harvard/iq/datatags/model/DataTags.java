package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A set of (tag type, value) pairs, describing how data sets should
 * be handled. DataTag objects are essentially maps from tag types to tag values.
 * It's slightly more complicated, of course, but that's the gist of it. 
 * 
 * @author michael
 */
public class DataTags {
	
	private final Map<TagType,TagValue> values = new HashMap<>();
	
	public void add( TagValue tv ) {
		values.put( tv.getType(), tv );
	}
	
	public void remove( TagType tt ) {
		values.remove(tt);
	}
	
	public TagValue get( TagType tt ) {
		return values.get(tt);
	}
	
	public Set<TagType> getTypes() {
		return Collections.unmodifiableSet(values.keySet());
	}

	/**
	 * @return a deep copy of the data tags.
	 */
	public DataTags makeCopy() {
		DataTags copy = new DataTags();
		
		for ( TagValue e : values.values() ) {
			copy.add( e.getOwnableInstance() );
		}
		
		return copy;
	}
	
	/**
	 * Returns a copy with values of both {@code this} and {@code other}.
	 * For each value type T, the composition is as follows:
	 * <ul>
	 *	<li>If only one tag contains a value for T, that value is used in the
	 *		result tags</li>
	 *  <li>Otherwise:
	 *		<ul><li>If the value is a simple value, the stricter value (i.e. higher ordinal) is chosen
	 *			    for the result tag.</li>
	 *			<li>If the value is an aggregate value, the result tag will contain
	 *			    the union of the two values</li>
	 *	    </ul>
	 *  </li>
	 * </ul>
	 * 
	 * Note: if {@code other} is {@code null}, this method behaves as {@link #makeCopy()}.
	 * 
	 * @param other
	 * @return A new DataTags object, composed from {@code this} and {@code other}.
	 */
	public DataTags composeWith( DataTags other ) {
		if ( other == null ) return makeCopy();
		
		edu.harvard.iq.datatags.model.values.TagValue.Visitor<TagValue.Function> resolver = new edu.harvard.iq.datatags.model.values.TagValue.Visitor<TagValue.Function>() {

			@Override
			public TagValue.Function visitSimpleValue( final SimpleValue op1 ) {
				return new TagValue.Function(){
					@Override public TagValue apply(TagValue v) {
						SimpleValue op2 = (SimpleValue) v;
						return ( op1.compareTo(op2) > 0 ? op1 : op2).getOwnableInstance();
				}};
			}

			@Override
			public TagValue.Function visitAggregateValue( final AggregateValue op1 ) {
				return new TagValue.Function(){
					@Override public TagValue apply(TagValue v) {
						AggregateValue op2 = (AggregateValue) v;
						AggregateValue res = op1.getOwnableInstance();
						for ( TagValue tv : op2.getValues() ) {
							res.add(tv);
						}
						return res;
				}};
			}

			@Override
			public TagValue.Function visitToDoValue( ToDoValue v ) {
				return new TagValue.Function() {
					@Override public TagValue apply(TagValue v) {
						return v;
				}};
		}};
		
		Set<TagType> types = new HashSet<>();
		types.addAll( getTypes() );
		types.addAll( other.getTypes() );
		DataTags result = new DataTags();
		
		// Composing. Note that for each type in types, at least one object has a non-null value
		for ( TagType tp : types ) {
			TagValue<?> ours = get(tp);
			TagValue<?> its  = other.get(tp);
			
			if ( ours == null ) {
				if ( its == null )
					throw new IllegalStateException( "Both [this] or [other] had null tag value for a tag type");
				else
					result.add( its );
			
			} else if ( its == null ) {
				result.add( ours );

			} else {
				result.add( ours.accept(resolver).apply(its) );
			}
		}
		
		return result;
		
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 97 * hash + Objects.hashCode(this.values);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof DataTags) ) {
			return false;
		}
		final DataTags other = (DataTags) obj;
		
		return Objects.equals(this.values, other.values);
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for ( Map.Entry<TagType, TagValue> e : values.entrySet() ) {
			sb.append(e.getKey().getName()).append(":").append(e.getValue().getName()).append(" ");
		}
		return "[DataTags {" + sb.toString() +"}]";
	}
	
}
