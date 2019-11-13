package edu.harvard.iq.policymodels.model.decisiongraph.nodes;

import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that tells the system to continue execution from the current section's
 * next node. In other words, skips the rest of the sections and moved on to 
 * whatever is after it.
 * 
 * @author michael
 */
public class ContinueNode extends TerminalNode {

    public ContinueNode(String anId) {
        super(anId);
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit( this );
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof ContinueNode) {
            return Objects.equals(getId(), ((Node) o).getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getId() != null ? getId().hashCode() : 0;
    }
    
}
