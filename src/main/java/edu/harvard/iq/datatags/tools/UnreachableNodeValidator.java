
package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/**
 * Checks that every node in the flow chart set
 * is reachable. Returns a WARNING if there is an unreachable
 * node.
 * @author Naomi
 */
public class UnreachableNodeValidator implements DecisionGraphValidator {
    
    private final List<ValidationMessage> validationMessages = new LinkedList<>();
    
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

        ReachableNodesCollector nc = new ReachableNodesCollector();
        dg.getStart().accept(nc);
        nc.getCollectedNodes().stream().map(n->n.getId()).forEach( flowChartNodeIds::remove );
        Set<String> flowChartNodeIdsEnds = flowChartNodeIds.stream().filter(n->n.endsWith("[SYN-END]")).collect(toSet());
        flowChartNodeIds.removeAll(flowChartNodeIdsEnds);

        if (!flowChartNodeIds.isEmpty()) {
            flowChartNodeIds.forEach((nodeId) -> {
                validationMessages.add(new NodeValidationMessage(Level.WARNING,
                        "Node \"" + nodeId + "\" is unreachable.",
                        dg.getNode(nodeId)));
            });
        }
        
        return validationMessages;
    }
    
}
