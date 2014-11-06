
package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node.NullVisitor;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * A class that checks that every node in the flow chart set
 * is reachable. Returns a WARNING if there is an unreachable
 * node.
 * @author Naomi
 */
public class UnreachableNodeValidator extends NullVisitor {
    
    private final LinkedList<ValidationMessage> validationMessages = new LinkedList<>();
    private final Set<String> reachedNodeIds = new HashSet<>();
    private FlowChart flowChart = new FlowChart();
    
    /**
     * Check each FlowChart in the FlowChartSet for unreachable nodes.
     * Begin from the start node identified by the FlowChart.
     * Return WARNING messages showing the unreachable nodes.
     */
    public LinkedList<ValidationMessage> validateUnreachableNodes(FlowChartSet fcs) {
        Set<String> flowChartNodeIds = new HashSet<>();
        for (FlowChart chart : fcs.charts()) {
            flowChartNodeIds.addAll( chart.nodeIds() );
            
            flowChart = chart;
            chart.getStart().accept(this);
            flowChartNodeIds.removeAll(reachedNodeIds);
            flowChartNodeIds.remove(flowChart.getEndNode().getId());
            
            if (!flowChartNodeIds.isEmpty()) {
                for (String nodeId : flowChartNodeIds) {
                    validationMessages.addLast(new ValidationMessage(Level.WARNING,
                                              "Node \"" + nodeId + "\" is unreachable.", chart.getNode(nodeId)));
                }
            }
        }
        
        return validationMessages;
    }
    
    
    /**
     * Check that the ask node and its answer nodes have not already been
     * traversed before iterating and recursing.
     */
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        for (Answer answer : nd.getAnswers()) {
            if (!reachedNodeIds.contains(nd.getNodeFor(answer).getId())) {
                nd.getNodeFor(answer).accept(this);
            }
        }
    }

    /**
     * Check that the set node and the next node have not already been
     * traversed before recursing.
     */
    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        if (!reachedNodeIds.contains(nd.getNextNode().getId())) {
            nd.getNextNode().accept(this);
        }
    }

    /**
     * Check that the reject node has not already been
     * traversed.
     */
    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
    }

    /**
     * Check that the call node, its callee node, and the next node have not
     * already been traversed before recursing.
     */
    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        if (!reachedNodeIds.contains(nd.getCalleeNodeId())) {
            flowChart.getNode(nd.getCalleeNodeId()).accept(this);
        }
        if (!reachedNodeIds.contains(nd.getNextNode().getId())) {
            nd.getNextNode().accept(this);
        }
    }

    /**
     * Check that the todo node and the next node have not already been
     * traversed before recursing.
     */
    @Override
    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        if (!reachedNodeIds.contains(nd.getNextNode().getId())) {
            nd.getNextNode().accept(this);
        }
    }

    /**
     * Check that the end node has not already been
     * traversed.
     */
    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
    }
    
}
