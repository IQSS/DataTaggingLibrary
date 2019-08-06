package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A reference to @{code [set YYY]} instruction.
 * 
 * @author Michael Bar-Sinai
 */
public class AstSetNode extends AstNode {
    
    public abstract static class Assignment {
        
        public interface Visitor {
            void visit( AtomicAssignment aa );
            void visit( AggregateAssignment aa );
        }
        
        final List<String> slot;
        
        public Assignment( List<String> aSlot ) {
            slot = aSlot;
        }
        
        public List<String> getSlot() {
            return slot;
        }
        
        public abstract void accept( Visitor v );
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
        public void accept( Visitor v ) {
            v.visit(this);
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
        public void accept( Visitor v ) {
            v.visit(this);
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
    
    private final List<Assignment> assignments;

    public AstSetNode(String id, List<Assignment> someAssignments) {
        super( id );
        assignments = someAssignments;
    }
    
    
    public Set<List<String>> getSlotNames() {
        return assignments.stream().map( a -> a.slot ).collect( Collectors.toSet() );
    }

    public List<Assignment> getAssignments() {
        return assignments;
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
        if ( ! ( obj instanceof AstSetNode) ) {
            return false;
        }
                
        final AstSetNode other = (AstSetNode) obj;
        return other.assignments.equals(assignments) && super.equalsAsAstNode(other);
    }
    
    @Override
    public String toStringExtras() {
        return "assignments:" + assignments;
    }
   
}
