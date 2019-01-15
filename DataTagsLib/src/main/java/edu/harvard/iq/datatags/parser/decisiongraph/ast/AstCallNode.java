package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AstCallNode extends AstNode {
    private final String calleeId;
    
    public AstCallNode( String calleeId ) {
        this( null, calleeId );
    }
    
    public AstCallNode( String id, String calleeId ) {
        super( id );
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
        if ( ! (obj instanceof AstCallNode) ) {
            return false;
        }
        final AstCallNode other = (AstCallNode) obj;
        if (!Objects.equals(this.calleeId, other.calleeId)) {
            return false;
        }
        return equalsAsAstNode(other);
    }
    
    @Override
    public String toString() {
        return "[CallNodeRef id:" + getId() + " callee:" + getCalleeId() + "]";
    }
    
}
