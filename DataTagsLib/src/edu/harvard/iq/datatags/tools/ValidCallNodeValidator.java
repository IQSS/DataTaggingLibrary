package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.Node.NullVisitor;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.LinkedList;

/**
 * Checks that every id referenced in a CallNode exists.
 * Returns an ERROR with each nonexistent node.
 * 
 * @author Naomi
 */
public class ValidCallNodeValidator extends NullVisitor {
    
    private final LinkedList<ValidationMessage> validationMessages = new LinkedList<>();
    private FlowChart chart;
    
    
    public LinkedList<ValidationMessage> validateIdReferences(FlowChartSet fcs) {
        Iterable<FlowChart> chartGroup = fcs.charts();
        for (FlowChart c : chartGroup) {
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
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Call node \"" + cn + "\" calls nonexistent node."));
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