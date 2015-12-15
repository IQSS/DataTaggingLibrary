
package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that every node in the flow chart set
 * is reachable. Returns a WARNING if there is an unreachable
 * node.
 * @author Naomi
 * @author Michael
 */
public class UnreachableNodeValidator extends VoidVisitor {
    
    private final Set<String> reachedNodeIds = new HashSet<>();
    private DecisionGraph flowChart = new DecisionGraph();
    
    /**
     * Check each FlowChart in the FlowChartSet for unreachable nodes.
     * Begin from the start node identified by the FlowChart.
     * @param dg The graph we validate.
     * @return WARNING messages showing the unreachable nodes.
     */
    public List<NodeValidationMessage> validateUnreachableNodes( DecisionGraph dg ) {
        final List<NodeValidationMessage> validationMessages = new LinkedList<>();
        Set<String> flowChartNodeIds = new HashSet<>();
        flowChartNodeIds.addAll( dg.nodeIds() );

        flowChart = dg;
        dg.getStart().accept(this);
        flowChartNodeIds.removeAll(reachedNodeIds);
        
        return flowChartNodeIds.stream()
                .map( nodeId -> new NodeValidationMessage(Level.WARNING,
                                "Node \"" + nodeId + "\" is unreachable.", dg.getNode(nodeId)) )
                .collect(Collectors.toList());
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
        if (!reachedNodeIds.contains(nd.getCalleeNodeId()) && flowChart.getNode(nd.getCalleeNodeId()) != null) {
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
