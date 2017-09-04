package edu.harvard.iq.datatags.visualizers.graphviz;

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
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import static java.util.stream.Collectors.joining;

/**
 * Base class for {@link DecisionGraph} visualizers, that use Graphviz.
 * @author michael
 */
public abstract class AbstractGraphvizDecisionGraphVisualizer extends GraphvizVisualizer {
    
    protected final static String START_NODE_NAME = "NODE___________START_";
    protected final Map<Character, String> idEncodeMap = new HashMap<>();
    protected DecisionGraph theGraph;
    
    Set<String> visitedIds = new TreeSet<>();
    
    protected abstract class AbstracctNodePainter extends Node.VoidVisitor {
        PrintWriter out;
        
        Set<String> visitedIds = new TreeSet<>();
        
        protected void advanceTo( Node nd ) {
            if ( ! visitedIds.contains(nd.getId()) ) {
                visitedIds.add(nd.getId());
                nd.accept(this);
            }
        }

        protected String idLabel(Node nd) {
            return AstNodeIdProvider.isAutoId(nd.getId()) ? "" : sanitizeIdDisplay(nd.getId()) + "\\n";
        }
    }
    
    protected final AbstractValue.Visitor<String> valueNamer = new AbstractValue.Visitor<String>() {
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
            for (AbstractSlot tt : aThis.getNonEmptySubSlots()) {
                sb.append(tt.getName());
                sb.append(":");
                sb.append(aThis.get(tt).accept(this));
                sb.append(" ");
            }
            if (!aThis.getNonEmptySubSlots().isEmpty()) {
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
    
    protected void drawCallLinks(PrintWriter out) {
        for ( Node nd : getDecisionGraph().nodes() ) {
            out.println("edge [constraint=false];");
            if ( nd instanceof CallNode ) {
                CallNode cn = (CallNode) nd;
                out.println( GvEdge.edge(nodeId(cn), nodeId(cn.getCalleeNode())).gv() );
            }
        }
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
    
    String sanitizeIdDisplay(String s){
        return s.replaceAll("\\\\", "\\\\\\\\");
    }
    
    protected String makeSameRank( Set<Node> nodes ) {
        StringBuilder sb = new StringBuilder();
        sb.append( "{rank=same ");
        sb.append( nodes.stream().map( nd->sanitizeId(nd.getId()) ).collect( joining(", ")));
        sb.append("}");
        
        return sb.toString();
    }
}
