package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Base class for {@link DecisionGraph} visualizers, that use Graphviz.
 * @author michael
 */
public abstract class AbstractGraphvizDecisionGraphVisualizer extends GraphvizVisualizer {

    protected final Map<Character, String> idEncodeMap = new HashMap<>();
    protected DecisionGraph theGraph;
    protected final TagValue.Visitor<String> valueNamer = new TagValue.Visitor<String>() {
        @Override
        public String visitToDoValue(ToDoValue v) {
            return v.getInfo();
        }

        @Override
        public String visitAtomicValue(AtomicValue v) {
            return v.getName();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            StringBuilder sb = new StringBuilder();
            sb.append("\\{");
            v.getValues().forEach((AtomicValue tv) -> sb.append(tv.accept(this)).append(","));
            if (!v.getValues().isEmpty()) {
                sb.setLength(sb.length() - 1);
            }
            sb.append("\\}");
            return sb.toString();
        }

        @Override
        public String visitCompoundValue(CompoundValue aThis) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for (SlotType tt : aThis.getNonEmptySubSlotTypes()) {
                sb.append(tt.getName());
                sb.append(":");
                sb.append(aThis.get(tt).accept(this));
                sb.append(" ");
            }
            if (!aThis.getNonEmptySubSlotTypes().isEmpty()) {
                sb.setLength(sb.length() - 1);
            }
            sb.append("]");
            return sb.toString();
        }
    };

    /**
     * Finds all the nodes that are the head of a subroutine/section, to the extent we
     * have those (we currently don't).
     * @param dg the FlowChart we search in
     * @return Set of all nodes to draw subcharts from.
     */
    protected Set<Node> findSubchartHeades(DecisionGraph dg) {
        final Set<Node> candidates = new HashSet<>();
        for (Node n : dg.nodes()) {
            candidates.add(n);
        }
        
        Node.VoidVisitor remover = new Node.VoidVisitor() {
            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                nd.getAnswers().stream().map( nd::getNodeFor )
                               .forEach( ansNode -> { 
                                   candidates.remove(ansNode); 
                                   ansNode.accept(this);
                               });
            }

            @Override
            public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
                nd.getAnswers().stream().map( nd::getNodeFor )
                               .forEach( ansNode -> { 
                                   candidates.remove(ansNode); 
                                   ansNode.accept(this);
                               });
                if ( nd.getElseNode() != null ) {
                    candidates.remove(nd.getElseNode());
                    nd.getElseNode().accept(this);
                }
            }

            @Override
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
                
                candidates.remove(nd.getStartNode());
                nd.getStartNode().accept(this);
            }

            /// Through nodes - remove next node, and then proceed using it
            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }

            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }
            
            /// Terminal nodes - Nothing to do.
            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
            }

            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            }
        };
        
        for (Node n : dg.nodes()) {
            if (candidates.contains(n)) {
                n.accept( remover );
            }
        }
        return candidates;
    }

    public DecisionGraph getDecisionGraph() {
        return theGraph;
    }
    
    public void setDecisionGraph(DecisionGraph aDecisionGraph) {
        theGraph = aDecisionGraph;
    }

    String nodeId(Node nd) {
        return sanitizeId(nd.getId());
    }
   
}
