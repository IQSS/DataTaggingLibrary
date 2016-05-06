package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * When a computation gets to {@code this} node, it is set to "reject" mode, and
 * halted. In effect, this means that the dataset cannot be accepted to the
 * repository,
 *
 * @author michael
 */
public class RejectNode extends TerminalNode {

    private final String reason;

    public RejectNode(String id, String reason) {
        super(id);
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit(this);
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 61 * hash + Objects.hashCode(this.reason);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof RejectNode)) {
            return false;
        }
        final RejectNode other = (RejectNode) obj;
        return Objects.equals(this.reason, other.reason) && equalsAsNode(other);
    }

    @Override
    public String toStringExtras() {
        return "reason:" + reason;
    }

}
