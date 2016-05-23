package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * A type whose values contain fields. As there is only one field of each type,
 * the fields are accessible by using the type as key.
 * <br>
 * Corollary: Instances of compound types are maps from types to values of those types.
 * 
 * @author michael
 */
public class CompoundSlot extends SlotType {
	private final Set<SlotType> fieldTypes = new HashSet<>();

	public CompoundSlot(String name, String note) {
		super(name, note);
	}
	
	public void addFieldType( SlotType tt ) {
		fieldTypes.add( tt );
	}
	
	public Set<SlotType> getFieldTypes() {
		return Collections.unmodifiableSet(fieldTypes);
	}
	
	public void removeFieldType( SlotType tt ) {
		fieldTypes.remove(tt);
	}
    
    public SlotType getTypeNamed( String typeName ) {
        for ( SlotType tt : getFieldTypes() ) {
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
		return v.visitCompoundSlot(this);
	}
    
    
    
}
