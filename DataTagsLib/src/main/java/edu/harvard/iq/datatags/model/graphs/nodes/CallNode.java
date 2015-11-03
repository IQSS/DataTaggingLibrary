package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that calls another node in a new execution frame.
 * @author michael
 */
public class CallNode extends ThroughNode {
	
	private String calleeNodeId;
	
	public CallNode(String id) {
		super(id);
	}

	public CallNode(String id, String calleeNodeId ) {
		super(id);
		this.calleeNodeId = calleeNodeId;
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit(this);
	}
	
	public String getCalleeNodeId() {
		return calleeNodeId;
	}

    public void setCalleeNodeId(String calleeNodeId) {
        this.calleeNodeId = calleeNodeId;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.calleeNodeId);
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
        final CallNode other = (CallNode) obj;
        if (!Objects.equals(this.calleeNodeId, other.calleeNodeId)) {
            return false;
        }
        return Objects.equals(getId(), other.getId());
    }

    @Override
    protected String toStringExtras() {
        return "callee:" + getCalleeNodeId();
    }
	
    
}
