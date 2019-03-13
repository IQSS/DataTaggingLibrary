package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that tells the system to continue execution from its container's next node.
 * As an example, when getting to a {@code ContinueNode} while in a {@link SectionNode},
 * execution continues from the section's {@link SectionNode#getNextNode()}.
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
    
}
