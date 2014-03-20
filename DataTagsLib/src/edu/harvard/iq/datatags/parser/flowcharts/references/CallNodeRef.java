package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class CallNodeRef extends InstructionNodeRef {
    private final String calleeId;
    
    public CallNodeRef( String calleeId ) {
        this( (String)null, calleeId );
    }
    public CallNodeRef( String id, String calleeId ) {
        super( new TypedNodeHeadRef(id, NodeType.Call));
        this.calleeId = calleeId;
    }
       
    public String getCalleeId() {
        return calleeId;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 23 * hash + Objects.hashCode(this.calleeId);
        return hash;
    }
	
	@Override
	public <T> T accept( Visitor<T> v ) {
		return v.visit(this);
	}

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof CallNodeRef) ) {
            return false;
        }
        final CallNodeRef other = (CallNodeRef) obj;
        if (!Objects.equals(this.calleeId, other.calleeId)) {
            return false;
        }
        return equalsAsInstructionNodeRef(other);
    }
    
    @Override
    public String toString() {
        return "[CallNodeRef id:" + getId() + " callee:" + getCalleeId() + "]";
    }
    
}
