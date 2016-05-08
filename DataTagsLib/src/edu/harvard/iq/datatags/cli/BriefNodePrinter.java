package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * Prints nodes to the console, with minimal details.
 * @author michael
 */
class BriefNodePrinter extends Node.VoidVisitor {
    
    private static final int WIDTH = 70;
    private final CliRunner rnr;

    public BriefNodePrinter(CliRunner rnr) {
        this.rnr = rnr;
    }
    
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< consider: ]", nd.getId());
    }
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< ask: %s]", nd.getId(), rnr.truncateAt(nd.getText(), WIDTH-nd.getId().length()));
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< set]", nd.getId());
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< reject: %s]", nd.getId(), rnr.truncateAt(nd.getReason(), WIDTH-nd.getId().length()-3));
    }

    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< call: %s]", nd.getId(), nd.getCalleeNodeId());
    }

    @Override
    public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< todo: %s]", nd.getId(), rnr.truncateAt(nd.getTodoText(), WIDTH-nd.getId().length()-1));
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        rnr.println("[>%s< end]", nd.getId());
    }
    
}
