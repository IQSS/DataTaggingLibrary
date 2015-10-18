package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.FlowChartSet;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.LinkedList;

/**
 * Checks that every id referenced in a CallNode exists.
 * Returns an ERROR with each nonexistent node.
 * 
 * @author Naomi
 */
public class ValidCallNodeValidator extends VoidVisitor {
    
    private final LinkedList<NodeValidationMessage> validationMessages = new LinkedList<>();
    private DecisionGraph chart;
    
    
    public LinkedList<NodeValidationMessage> validateIdReferences(FlowChartSet fcs) {
        Iterable<DecisionGraph> chartGroup = fcs.charts();
        for (DecisionGraph c : chartGroup) {
            chart = c;
            Iterable<Node> chartNodes = chart.nodes();
            for (Node allnodes : chartNodes) {
                allnodes.accept(this);
            }
        }
        return validationMessages;
    }
    
    
    @Override
    public void visitImpl (CallNode cn) throws DataTagsRuntimeException {
        Node exists = chart.getNode(cn.getCalleeNodeId());
        if (exists == null) {
            validationMessages.addLast(new NodeValidationMessage(Level.ERROR, "Call node \"" + cn + "\" calls nonexistent node."));
        }
    }
    
    @Override
    public void visitImpl (AskNode cn) throws DataTagsRuntimeException {
        // do nothing
    }
    
    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    
}