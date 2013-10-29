package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A type whose values contain fields. As there is only one field of each type,
 * the fields are accessible by using the type as key.
 * 
 * @author michael
 */
public class CompoundType extends TagType {
	private final Set<TagType> fieldTypes = new HashSet<>();

	public CompoundType(String name, String info) {
		super(name, info);
	}
	
	public void addFieldType( TagType tt ) {
		fieldTypes.add( tt );
	}
	
	public Set<TagType> getFieldTypes() {
		return Collections.unmodifiableSet(fieldTypes);
	}

	@Override
	public CompoundValue make(String name, String info) {
		return new CompoundValue(name, this, info);
	}

}
