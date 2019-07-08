package edu.harvard.iq.datatags.parser.decisiongraph.ast;

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
    
    
    
    
    private final List<SlotValuePair> assignments;

    public AstSetNode(String id, List<SlotValuePair> someAssignments) {
        super( id );
        assignments = someAssignments;
    }
    
    
    public Set<List<String>> getSlotNames() {
        return assignments.stream().map( a -> a.slot ).collect( Collectors.toSet() );
    }

    public List<SlotValuePair> getAssignments() {
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
