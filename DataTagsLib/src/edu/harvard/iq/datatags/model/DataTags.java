package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;

/**
 * A set of (tag type, value) pairs, describing how data sets should
 * be handled. DataTag objects are essentially maps from tag types to tag values.
 * It's slightly more complicated, of course, but that's the gist of it. <br />
 * DataTags are a special type of {@link CompoundValue}, in that they exclusively own
 * their type. Changing tags to a DataTags object updates the data tag and it's type. 
 * To contrast, adding value {@code v} to a regular compound value {@code c},
 * when the type of {@code c} does allow for values of type {@code v.getType()},
 * results in a runtime error.
 * 
 * @author michael
 */
public class DataTags extends CompoundValue {
	
	/** A TagValue visitor used in the composing process. */
	private final static Resolver RESOLVER = new Resolver();

	public DataTags() {
		super("DataTags", new CompoundType("DataTagsType",null), null);
	}

	@Override
	public void clear(TagType type) {
		super.clear(type);
		getType().removeFieldType(type);
	}

	@Override
	public void set(TagValue value) {
		getType().addFieldType(value.getType());
		super.set(value);
	}

	@Override
	public TagValue get(TagType type) {
		return getType().getFieldTypes().contains(type)
					? super.get(type)
					: null;
				
	}
	
	@Override
	public DataTags getOwnableInstance() {
		return buildOwnableInstance( new DataTags() );
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
	 *			<li>If the value is a compound value, the result will be calculated
	 *				recursively. </li>
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
		if ( other == null ) return getOwnableInstance();
		
		DataTags result = new DataTags();
		
		// Composing. Note that for each type in types, at least one object has a non-null value
		for ( TagType tp : C.unionSet(getSetFieldTypes(), other.getSetFieldTypes()) ) {
			TagValue<?> ours = get(tp);
			TagValue<?> its  = other.get(tp);
			
			if ( ours == null ) {
				if ( its == null )
					throw new IllegalStateException( "Both [this] and [other] had null tag value for a tag type");
				else
					result.set( its );
			
			} else if ( its == null ) {
				result.set( ours );

			} else {
				result.set( ours.accept(RESOLVER).apply(its) );
			}
		}
		
		return result;
	}
}

class Resolver implements TagValue.Visitor<TagValue.Function> {

	@Override
	public TagValue.Function visitSimpleValue( final SimpleValue op1 ) {
		return new TagValue.Function(){
			@Override public TagValue apply(TagValue v) {
				if ( v==null ) return op1.getOwnableInstance();
				SimpleValue op2 = (SimpleValue) v;
				return ( op1.compareTo(op2) > 0 ? op1 : op2).getOwnableInstance();
		}};
	}

	@Override
	public TagValue.Function visitAggregateValue( final AggregateValue op1 ) {
		return new TagValue.Function(){
			@Override public TagValue apply(TagValue v) {
				AggregateValue res = op1.getOwnableInstance();
				if ( v==null ) return res;
				AggregateValue op2 = (AggregateValue) v;
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
	}

	@Override
	public TagValue.Function visitCompoundValue( final CompoundValue cv ) {
		return new TagValue.Function() {
			@Override public TagValue apply(TagValue v) {
				CompoundValue res = cv.getOwnableInstance();
				if ( v==null ) return res;
				CompoundValue cv2 = (CompoundValue) v;
				for ( TagType tt : C.unionSet(cv2.getSetFieldTypes(), cv.getSetFieldTypes())) {
					res.set(
							(res.get(tt)==null) ? cv2.get(tt)
									: ((TagValue.Function)cv.get(tt).accept(Resolver.this)).apply(cv2.get(tt)));
				}
				return res;
		}};
	}
}
