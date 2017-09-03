package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A type that a slot may have.
 * 
 * @author michael
 */
public abstract class AbstractSlot {
	
	public interface Visitor<T> {
		T visitSimpleSlot( AtomicSlot t );
		T visitAggregateSlot( AggregateSlot t );
		T visitCompoundSlot( CompoundSlot t );
		T visitTodoSlot( ToDoSlot t );
	}
	
    public abstract static class VoidVisitor implements Visitor<Void> {
        @Override
        public Void visitSimpleSlot( AtomicSlot t ) {
            visitAtomicSlotImpl(t);
            return null;
        }
        
        @Override
        public Void visitAggregateSlot( AggregateSlot t ) {
            visitAggregateSlotImpl(t);
            return null;
        }
        
        @Override
        public Void visitCompoundSlot( CompoundSlot t ) {
            visitCompoundSlotImpl(t);
            return null;
        }
        
        @Override
        public Void visitTodoSlot( ToDoSlot t ) {
            visitTodoSlotImpl(t);
            return null;
        }

        public abstract void visitAtomicSlotImpl( AtomicSlot t );
        public abstract void visitAggregateSlotImpl( AggregateSlot t );
        public abstract void visitCompoundSlotImpl( CompoundSlot t );
        public abstract void visitTodoSlotImpl( ToDoSlot t );
    }
    
	private final String name;
	private String note;

	public AbstractSlot(String name, String note) {
		this.name = name;
		this.note = note;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}
	
	public String getName() {
		return name;
	}

	public abstract <T> T accept( Visitor<T> v );
	
    public SlotValueLookupResult lookupValue( final String slotName, final String valueName ) {
        return accept(new AbstractSlot.Visitor<SlotValueLookupResult>() {
            
            @Override
            public SlotValueLookupResult visitSimpleSlot(AtomicSlot t) {
                if ( slotName.equals(t.getName()) ) {
                    AbstractValue v = t.valueOf( valueName );
                    return (v!=null) ? SlotValueLookupResult.Success(v)
                                     : SlotValueLookupResult.ValueNotFound(t, valueName);
                } else {
                    return SlotValueLookupResult.SlotNotFound(slotName);
                }
            }

            @Override
            public SlotValueLookupResult visitAggregateSlot(AggregateSlot t) {
                if ( slotName.equals(t.getName()) ) {
                    AggregateValue res = t.createInstance();
                    AtomicValue singleValue = t.getItemType().valueOf(valueName);
                    
                    if ( singleValue == null ) {
                        return SlotValueLookupResult.ValueNotFound(AbstractSlot.this, valueName);
                    } else {
                        res.add(singleValue);
                        return SlotValueLookupResult.Success(res);
                    }
                    
                } else {
                    return SlotValueLookupResult.SlotNotFound(slotName);
                }
            }

            @Override
            public SlotValueLookupResult visitCompoundSlot(CompoundSlot t) {
                final List<SlotValueLookupResult.Success> matches = new LinkedList<>();
                final AtomicReference<SlotValueLookupResult.ValueNotFound> vnfRef = new AtomicReference<>(null);
                
                SlotValueLookupResult.VoidVisitor aggregator = new SlotValueLookupResult.VoidVisitor() {

                    @Override
                    protected void visitImpl(SlotValueLookupResult.SlotNotFound snf) {}

                    @Override
                    protected void visitImpl(SlotValueLookupResult.ValueNotFound vnf) {
                        vnfRef.set(vnf);
                    }

                    @Override
                    protected void visitImpl(SlotValueLookupResult.Ambiguity amb) {
                        matches.addAll( amb.getPossibilities() );
                    }

                    @Override
                    protected void visitImpl(SlotValueLookupResult.Success scss) {
                        matches.add( scss );
                    }
                };
                
                // group results by status.
                for ( AbstractSlot tt : t.getSubSlots() ) {
                    tt.accept(this) // get the lookup result
                      .accept(aggregator); // process the lookup result
                }
                
                switch ( matches.size() ) {
                    case 0:
                        return (vnfRef.get()==null)
                                ? SlotValueLookupResult.SlotNotFound(slotName)
                                : vnfRef.get();
                    case 1: 
                        return matches.get(0);
                        
                    default: 
                        return SlotValueLookupResult.Ambiguity(matches);
                }
            }

            @Override
            public SlotValueLookupResult visitTodoSlot(ToDoSlot t) {
                if ( slotName.equals(t.getName())) {
                    return SlotValueLookupResult.Success(t.getValue());
                } else {
                    return SlotValueLookupResult.SlotNotFound(slotName);
                }
            }
        });
    }
    
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(this.name);
		hash = 89 * hash + Objects.hashCode(this.note);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof AbstractSlot) ) {
			return false;
		}
		final AbstractSlot other = (AbstractSlot) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
	
		return Objects.equals(this.note, other.note);
	}

	@Override
	public String toString() {
		String[] className = getClass().getName().split("\\.");
		return String.format("[%s name:%s]", className[className.length-1], getName());
	}
	
	
}
