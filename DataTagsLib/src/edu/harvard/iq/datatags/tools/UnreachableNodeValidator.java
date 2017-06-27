
package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Checks that every node in the flow chart set
 * is reachable. Returns a WARNING if there is an unreachable
 * node.
 * @author Naomi
 */
public class UnreachableNodeValidator extends VoidVisitor implements DecisionGraphValidator {
    
    private final List<ValidationMessage> validationMessages = new LinkedList<>();
    private final Set<String> reachedNodeIds = new HashSet<>();
    private DecisionGraph flowChart = new DecisionGraph();
    
    /**
     * Check each FlowChart in the FlowChartSet for unreachable nodes.
     * Begin from the start node identified by the FlowChart.
     * @param dg The graph we validate.
     * @return WARNING messages showing the unreachable nodes.
     */
    @Override
    public List<ValidationMessage> validate( DecisionGraph dg ) {
        Set<String> flowChartNodeIds = new HashSet<>();
        flowChartNodeIds.addAll( dg.nodeIds() );

        flowChart = dg;
        dg.getStart().accept(this);
        flowChartNodeIds.removeAll(reachedNodeIds);
        flowChartNodeIds.remove("[SYN-END]");

        if (!flowChartNodeIds.isEmpty()) {
            flowChartNodeIds.forEach((nodeId) -> {
                validationMessages.add(new NodeValidationMessage(Level.WARNING,
                        "Node \"" + nodeId + "\" is unreachable.",
                        dg.getNode(nodeId)));
            });
        }
        
        return validationMessages;
    }
    
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        for (ConsiderAnswer answer : nd.getAnswers()) {
            if (!reachedNodeIds.contains(nd.getNodeFor(answer).getId())) {
                nd.getNodeFor(answer).accept(this);
            }
        }
        Node elseNode = nd.getElseNode();
        if ( ! reachedNodeIds.contains(elseNode.getId()) ) {
            elseNode.accept(this);
        }
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
        if (!reachedNodeIds.contains(nd.getCalleeNode()) && nd.getCalleeNode() != null) {
            reachedNodeIds.add(nd.getCalleeNode().getId());
            nd.getCalleeNode().accept(this);
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
    public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
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
    
    @Override
    public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
        if (!reachedNodeIds.contains(nd.getId())) {
            reachedNodeIds.add(nd.getId());
        }
        if (!reachedNodeIds.contains(nd.getStartNode().getId())) {
            nd.getStartNode().accept(this);
        }
        if (!reachedNodeIds.contains(nd.getNextNode().getId())) {
            nd.getNextNode().accept(this);
        }
    }
    
    
}
