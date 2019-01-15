package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that pops the execution stack, if and when the execution gets to it.
 * May terminate a run, if the stack becomes empty.
 *
 * @author michael
 */
public class EndNode extends TerminalNode {

    public EndNode(String id) {
        super(id);
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit(this);
    }
    
    
    
    @Override
    public String toString() {
        return "[EndNode id:" + getId() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) {
            return false;
        }
        if (o instanceof EndNode) {
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
