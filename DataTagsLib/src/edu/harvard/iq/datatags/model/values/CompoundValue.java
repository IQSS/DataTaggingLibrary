package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 *
 * @author michael
 */
public class CompoundValue extends TagValue<CompoundType> {

	private final Map<TagType, TagValue> fields = new HashMap<>();
		
	public CompoundValue(String name, CompoundType type, String info) {
		super(name, type, info);
	}
	
	public void setField( TagValue value ) {
		if ( getType().getFieldTypes().contains(value.getType()) ) {
			fields.put(value.getType(), value);
		} else {
			throw new IllegalArgumentException( "Type " + getType() + " does not have a field of type " + value.getType() + ".");
		}
	}
	
	public void clearField( TagType type ) {
		fields.remove(type);
	}

	public TagValue getField( TagType type ) {
		if ( getType().getFieldTypes().contains(type) ) {
			return fields.get(type);
		} else {
			throw new IllegalArgumentException( "Type " + getType() + " does not have a field of type " + type + ".");
		}
	}
	
	public Set<TagType> getSetFields() {
		return fields.keySet();
	}
	
	@Override
	public <R> R accept(Visitor<R> visitor) {
		return visitor.visitCompoundValue( this );
	}
	
	@Override
	public CompoundValue getOwnableInstance() {
		CompoundValue out = new CompoundValue( getName(), getType(), getInfo() );
		for ( TagType tt : getSetFields() ) {
			out.setField(getField(tt).getOwnableInstance());
		}
		
		return out;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 19 * hash + Objects.hashCode(this.fields);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( !(obj instanceof CompoundValue) ) {
			return false;
		}
		final CompoundValue other = (CompoundValue) obj;
		return super.equals(obj) && Objects.equals(this.fields, other.fields);
	}
	
	
}
