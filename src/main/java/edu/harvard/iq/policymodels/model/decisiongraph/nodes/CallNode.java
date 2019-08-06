package edu.harvard.iq.policymodels.model.decisiongraph.nodes;

import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that calls another node in a new execution frame.
 *
 * @author michael
 */
public class CallNode extends ThroughNode {

    private Node calleeNode;

    public CallNode(String id) {
        super(id);
    }

    public CallNode(String id, Node calleeNode) {
        super(id);
        this.calleeNode = calleeNode;
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit(this);
    }

    public Node getCalleeNode() {
        return calleeNode;
    }

    public void setCalleeNode(Node calleeNode) {
        this.calleeNode = calleeNode;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
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
        if (!Objects.equals(getId(), other.getId())) {
            return false;
        }
        if (!(calleeNode != null ? calleeNode.getId().equals(other.calleeNode != null ? other.calleeNode.getId() : calleeNode == null) : other.calleeNode == null)) {
            return false;
        }
        if (!(getNextNode() != null ? getNextNode().getId().equals(other.getNextNode() != null ? other.getNextNode().getId() : getNextNode() == null) : other.getNextNode() == null)) {
            return false;
        }
        return true;
    }

    @Override
    protected String toStringExtras() {
        return getCalleeNode() != null ? "callee:" + getCalleeNode().getId() : "callee null";
    }

}
