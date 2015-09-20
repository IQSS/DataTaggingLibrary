package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A type whose values contain fields. As there is only one field of each type,
 * the fields are accessible by using the type as key.
 * <br>
 * Corollary: Instances of compound types are maps from types to fields of those types.
 * 
 * @author michael
 */
public class CompoundType extends TagType {
	private final Set<TagType> fieldTypes = new HashSet<>();

	public CompoundType(String name, String note) {
		super(name, note);
	}
	
	public void addFieldType( TagType tt ) {
		fieldTypes.add( tt );
	}
	
	public Set<TagType> getFieldTypes() {
		return Collections.unmodifiableSet(fieldTypes);
	}
	
	public void removeFieldType( TagType tt ) {
		fieldTypes.remove(tt);
	}
    
    public TagType getTypeNamed( String typeName ) {
        for ( TagType tt : getFieldTypes() ) {
            if ( tt.getName().equals(typeName) ) {
                return tt;
            }
        }
        return null;
    }
    
    public CompoundValue createInstance() {
        return new CompoundValue( this );
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitCompoundType(this);
	}

}
