package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class SetNodeRef extends InstructionNodeRef {
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
