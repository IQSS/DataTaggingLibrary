package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
	private final Map<String,AbstractSlot> subSlots = new LinkedHashMap<>();

	public CompoundSlot(String name, String note) {
		super(name, note);
	}
	
	public void addSubSlot( AbstractSlot tt ) {
        if ( tt == null ) throw new IllegalArgumentException("Cannot add a null slot");
		subSlots.put( tt.getName(), tt );
	}
	
	public Set<AbstractSlot> getSubSlots() {
		return Collections.unmodifiableSet(new HashSet<>(subSlots.values()));
	}
	
	public void removeSubSlot( AbstractSlot tt ) {
		subSlots.remove(tt.getName());
	}
    
    public AbstractSlot getSubSlot( String subSlotName ) {
        return subSlots.get(subSlotName);
    }
    
    public AbstractSlot getSubSlot( List<String> subSlotPath ) {
        if ( subSlotPath.isEmpty() ) {
            return this;
        } else {
            AbstractSlot next = getSubSlot(C.head(subSlotPath));
            if ( next == null ) return null;
            if ( next instanceof CompoundSlot ) {
                return ((CompoundSlot)next).getSubSlot(C.tail(subSlotPath));
            } else {
                if ( subSlotPath.size() == 1  ) {
                    return next;
                } else {
                    return null;
                }
            }
            
        }
    }
    
    public CompoundValue createInstance() {
        return new CompoundValue( this );
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitCompoundSlot(this);
	}
    
    
    
}
