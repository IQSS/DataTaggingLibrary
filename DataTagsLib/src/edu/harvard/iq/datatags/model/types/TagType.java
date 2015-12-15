package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * A type of a value a data tag may have.
 * 
 * @author michael
 */
public abstract class TagType {
	
	public interface Visitor<T> {
		T visitSimpleType( AtomicType t );
		T visitAggregateType( AggregateType t );
		T visitCompoundType( CompoundType t );
		T visitTodoType( ToDoType t );
	}
	
    public abstract static class VoidVisitor implements TagType.Visitor<Void> {
        @Override
        public Void visitSimpleType( AtomicType t ) {
            visitAtomicTypeImpl(t);
            return null;
        }
        
        @Override
        public Void visitAggregateType( AggregateType t ) {
            visitAggregateTypeImpl(t);
            return null;
        }
        
        @Override
        public Void visitCompoundType( CompoundType t ) {
            visitCompoundTypeImpl(t);
            return null;
        }
        
        @Override
        public Void visitTodoType( ToDoType t ) {
            visitTodoTypeImpl(t);
            return null;
        }

        public abstract void visitAtomicTypeImpl( AtomicType t );
        public abstract void visitAggregateTypeImpl( AggregateType t );
        public abstract void visitCompoundTypeImpl( CompoundType t );
        public abstract void visitTodoTypeImpl( ToDoType t );
    }
    
	private final String name;
	private String note;

	public TagType(String name, String note) {
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

	public abstract <T> T accept( TagType.Visitor<T> v );
	
    public TagValueLookupResult lookupValue( final String slotName, final String valueName ) {
        return accept(new TagType.Visitor<TagValueLookupResult>() {
            
            @Override
            public TagValueLookupResult visitSimpleType(AtomicType t) {
                if ( slotName.equals(t.getName()) ) {
                    TagValue v = t.valueOf( valueName );
                    return (v!=null) ? TagValueLookupResult.Success(v)
                                     : TagValueLookupResult.ValueNotFound(t, valueName);
                } else {
                    return TagValueLookupResult.SlotNotFound(slotName);
                }
            }

            @Override
            public TagValueLookupResult visitAggregateType(AggregateType t) {
                if ( slotName.equals(t.getName()) ) {
                    AggregateValue res = t.createInstance();
                    AtomicValue singleValue = t.getItemType().valueOf(valueName);
                    
                    if ( singleValue == null ) {
                        return TagValueLookupResult.ValueNotFound(TagType.this, valueName);
                    } else {
                        res.add(singleValue);
                        return TagValueLookupResult.Success(res);
                    }
                    
                } else {
                    return TagValueLookupResult.SlotNotFound(slotName);
                }
            }

            @Override
            public TagValueLookupResult visitCompoundType(CompoundType t) {
                final List<TagValueLookupResult.Success> matches = new LinkedList<>();
                final AtomicReference<TagValueLookupResult.ValueNotFound> vnfRef = new AtomicReference<>(null);
                
                TagValueLookupResult.VoidVisitor aggregator = new TagValueLookupResult.VoidVisitor() {

                    @Override
                    protected void visitImpl(TagValueLookupResult.SlotNotFound snf) {}

                    @Override
                    protected void visitImpl(TagValueLookupResult.ValueNotFound vnf) {
                        vnfRef.set(vnf);
                    }

                    @Override
                    protected void visitImpl(TagValueLookupResult.Ambiguity amb) {
                        matches.addAll( amb.getPossibilities() );
                    }

                    @Override
                    protected void visitImpl(TagValueLookupResult.Success scss) {
                        matches.add( scss );
                    }
                };
                
                // group results by status.
                for ( TagType tt : t.getFieldTypes() ) {
                    tt.accept(this) // get the lookup result
                      .accept(aggregator); // process the lookup result
                }
                
                switch ( matches.size() ) {
                    case 0:
                        return (vnfRef.get()==null)
                                ? TagValueLookupResult.SlotNotFound(slotName)
                                : vnfRef.get();
                    case 1: 
                        return matches.get(0);
                        
                    default: 
                        return TagValueLookupResult.Ambiguity(matches);
                }
            }

            @Override
            public TagValueLookupResult visitTodoType(ToDoType t) {
                if ( slotName.equals(t.getName())) {
                    return TagValueLookupResult.Success(t.getValue());
                } else {
                    return TagValueLookupResult.SlotNotFound(slotName);
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
		if ( ! (obj instanceof TagType) ) {
			return false;
		}
		final TagType other = (TagType) obj;
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
