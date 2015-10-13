package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class SetNodeRef extends InstructionNodeRef {
    
    public abstract static class Assignment {
        final List<String> slot;
        
        public Assignment( List<String> aSlot ) {
            slot = aSlot;
        }
        
        public List<String> getSlot() {
            return slot;
        }
    }
    
    public static class AtomicAssignment extends Assignment {
        final String value;
        public AtomicAssignment( List<String> slot, String aValue ) {
            super( slot );
            value = aValue;
        }

        public String getValue() {
            return value;
        }
        @Override
        public String toString() {
            return "[" + getSlot() + "=" + getValue() + "]";
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AtomicAssignment other = (AtomicAssignment) obj;
            return Objects.equals(this.value, other.value) && Objects.equals(getSlot(), other.getSlot());
        }
        
    }
    
    public static class AggregateAssignment extends Assignment {
        final List<String> value;
        public AggregateAssignment( List<String> slot, List<String> aValue ) {
            super( slot );
            value = aValue;
        }

        public List<String> getValue() {
            return value;
        }
        @Override
        public String toString() {
            return "[" + getSlot() + "+=" + getValue() + "]";
        }
        
        @Override
        public int hashCode() {
            int hash = 5;
            hash = 59 * hash + Objects.hashCode(this.value);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final AggregateAssignment other = (AggregateAssignment) obj;
            return Objects.equals(this.value, other.value) && Objects.equals(getSlot(), other.getSlot());
        }
    }
    
    private final Map<String, String> assignments = new HashMap<>();

    public SetNodeRef(String id) {
        super( id );
    }
    
    public void addAssignment( String slotName, String value ) {
        assignments.put( slotName, value );
    }
    
    public Set<String> getSlotNames() {
        return assignments.keySet();
    }
    
    public String getValue( String slotName ) {
        return assignments.get(slotName);
    }
	
	@Override
	public <T> T accept( Visitor<T> v ) {
		return v.visit(this);
	}
    
	@Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 97 * hash + Objects.hashCode(this.assignments);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! ( obj instanceof SetNodeRef) ) {
            return false;
        }
                
        final SetNodeRef other = (SetNodeRef) obj;
        return other.assignments.equals(assignments) && super.equalsAsInstructionNodeRef(other);
    }
    
    @Override
    public String toString() {
        return "[SetNodeRef " + super.toString() + " assignments:" + assignments + "]";
    }
   
}
