package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * A slot whose values contains sub-slots (fields). As there is only one field of each type,
 * the fields are accessible by using the type as key.
 * <br>
 * Corollary: Instances of compound types are maps from types to values of those types.
 * 
 * @author michael
 */
public class CompoundSlot extends AbstractSlot {
	private final Set<AbstractSlot> subSlots = new LinkedHashSet<>();

	public CompoundSlot(String name, String note) {
		super(name, note);
	}
	
	public void addSubSlot( AbstractSlot tt ) {
		subSlots.add( tt );
	}
	
	public Set<AbstractSlot> getSubSlots() {
		return Collections.unmodifiableSet(subSlots);
	}
	
	public void removeSubSlot( AbstractSlot tt ) {
		subSlots.remove(tt);
	}
    
    public AbstractSlot getSubSlot( String typeName ) {
        for ( AbstractSlot tt : getSubSlots() ) {
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
