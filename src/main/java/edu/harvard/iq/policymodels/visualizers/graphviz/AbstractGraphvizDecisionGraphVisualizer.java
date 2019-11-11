package edu.harvard.iq.policymodels.visualizers.graphviz;

import edu.harvard.iq.policymodels.model.decisiongraph.Answer;
import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.AskNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.CallNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ConsiderNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ContainerNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ContinueNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.EndNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.PartNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.RejectNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SectionNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SetNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ThroughNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ToDoNode;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AggregateValue;
import edu.harvard.iq.policymodels.model.policyspace.values.AtomicValue;
import edu.harvard.iq.policymodels.model.policyspace.values.CompoundValue;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import edu.harvard.iq.policymodels.model.policyspace.values.ToDoValue;
import edu.harvard.iq.policymodels.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.policymodels.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvEdge.edge;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toSet;
import java.util.stream.StreamSupport;

/**
 * Base class for {@link DecisionGraph} visualizers, that use Graphviz.
 * @author michael
 */
public abstract class AbstractGraphvizDecisionGraphVisualizer extends GraphvizVisualizer {
    
    protected final static String START_NODE_NAME = "NODE___________START_";
    protected final Map<Character, String> idEncodeMap = new HashMap<>();
    protected DecisionGraph theGraph;
    
    /** 
     * Draws [end] nodes. Good for structural debugging, but otherwise adds a lot of
     * visual noise.
     */
    protected boolean drawEndNodes = false;
    
    /** Draw links between call nodes and their calees. */
    private boolean drawCallLinks = false;
    
    Set<String> visitedIds = new TreeSet<>();

    protected void drawCallLinks(PrintWriter out) {
        out.println("edge [constraint=false, color=\"#CCCCFF\", penwidth=2, style=dotted, arrowhead=open];");
        for (Node nd : getDecisionGraph().nodes()) {
            if (nd instanceof CallNode) {
                CallNode cn = (CallNode) nd;
                Node dest = cn.getCalleeNode();
                if (dest instanceof PartNode) {
                    dest = ((PartNode) dest).getStartNode();
                }
                if (dest != null) {
                    out.println(makeEdge(cn, dest).add("lhead", "cluster_" + sanitizeId(cn.getCalleeNode().getId())).gv());
                }
            }
        }
    }

    public void setDrawCallLinks(boolean drawCallLinks) {
        this.drawCallLinks = drawCallLinks;
    }

    public boolean isDrawCallLinks() {
        return drawCallLinks;
    }

    public boolean isDrawEndNodes() {
        return drawEndNodes;
    }
    
    protected abstract class AbstractNodePainter extends Node.VoidVisitor {
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
        candidates.remove( dg.getNode(DecisionGraphCompiler.SYNTHETIC_END_NODE_ID) );
        
        Node.VoidVisitor remover = new Node.VoidVisitor() {
            @Override
            public void visitImpl(AskNode nd) {
                nd.getAnswers().stream().map( nd::getNodeFor )
                               .forEach( ansNode -> { 
                                   candidates.remove(ansNode); 
                                   ansNode.accept(this);
                               });
            }

            @Override
            public void visitImpl(ConsiderNode nd) {
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
            public void visitImpl(SectionNode nd) {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
                
                candidates.remove(nd.getStartNode());
                nd.getStartNode().accept(this);
            }
            
            @Override
            public void visitImpl(PartNode nd) {
                candidates.remove(nd.getStartNode());
                nd.getStartNode().accept(this);
            }

            /// Through nodes - remove next node, and then proceed using it
            @Override
            public void visitImpl(SetNode nd) {
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }

            @Override
            public void visitImpl(CallNode nd){
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }

            @Override
            public void visitImpl(ToDoNode nd){
                candidates.remove(nd.getNextNode());
                nd.getNextNode().accept(this);
            }
            
            /// Terminal nodes - Nothing to do.
            @Override
            public void visitImpl(RejectNode nd) {}

            @Override
            public void visitImpl(EndNode nd) {}
            
            @Override
            public void visitImpl(ContinueNode nd ) {}

        };
        
        for (Node n : dg.nodes()) {
            if (candidates.contains(n)) {
                n.accept( remover );
            }
        }
        
        return candidates;
    }
    
    /**
     * Returns a map of nodes, where each key-value pair is an edge that, if added, 
     * ensures that section clusters come one below the other, not side-by-side.
     * @return A map fo nodes to be translated to invisible edges.
     */
    protected Map<Node, Node> findPostSectionEdges() {
        
        Set<SectionNode> sectionNodesToConsider = StreamSupport.stream(theGraph.nodes().spliterator(), false)
            .filter( n->n instanceof SectionNode )
            .map( n -> (SectionNode)n )
            .filter( n->shouldLinkTo(n.getNextNode()))
            .collect( toSet() );
        
        Map<Node,Node> retVal = new HashMap<>();
        
        sectionNodesToConsider.forEach( sec -> 
            findTerminalDrawnNodes(sec).forEach( tn -> 
                retVal.put(tn, sec.getNextNode())));
        
        return retVal;
    }
    
    private Set<Node> findTerminalDrawnNodes( SectionNode sn ) {
        final Set<Node> retVal = new HashSet<>();
        
        sn.getStartNode().accept( new Node.VoidVisitor() {
            @Override
            public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
                Set<Node> nexts = nd.getAnswers().stream().map( nd::getNodeFor )
                                    .filter( n -> shouldLinkTo(n) )
                                    .collect(toSet());
                if ( nd.getElseNode()!=null && shouldLinkTo(nd.getElseNode()) ){
                    nexts.add(nd.getElseNode());
                }
                if ( nexts.isEmpty() ) {
                    retVal.add(nd);
                } else {
                    nexts.forEach( n->n.accept(this) );
                }
            }

            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                Set<Node> nexts = nd.getAnswers().stream().map( nd::getNodeFor )
                                    .filter( n -> shouldLinkTo(n) )
                                    .collect(toSet());
                if ( nexts.isEmpty() ) {
                    retVal.add(nd);
                } else {
                    nexts.forEach( n->n.accept(this) );
                }
            }

            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }

            @Override
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                if ( shouldLinkTo(nd.getNextNode()) ) {
                    nd.getNextNode().accept(this);
                } else {
                    nd.getStartNode().accept(this);
                }
            }

            @Override
            public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
                retVal.add(nd);
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                retVal.add(nd);
            }

            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }

            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                retVal.add(nd);
            }

            @Override
            public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {
                retVal.add(nd);
            }
            
            private void visitThroughNode( ThroughNode tn ) {
                if ( shouldLinkTo(tn.getNextNode()) ) {
                    tn.getNextNode().accept(this);
                } else {
                    retVal.add(tn);
                }
            }
            
            private void visitNext( Node nd ) {
                if ( shouldLinkTo(nd) ) {
                    nd.accept(this);
                } else {
                    retVal.add(nd);
                }
            }
        });
        
        return retVal;
    }
    
    /**
     * Given a node in the graph, finds a deepest node that is still being drawn 
     * by this visualizer. Note that there may be more than a single such node.
     * @param start The node to start the traversal from
     * @return A node whose distance from start is the largest.
     */
    protected Node findDeepestDrawnNode( Node start ) {
        AtomicInteger maxDepth = new AtomicInteger(0);
        Node[] aDeepest = new Node[]{start};
        
        start.accept( new Node.VoidVisitor() {
            
            int curDepth = 0;
            
            @Override
            public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
                for ( CompoundValue a : nd.getAnswers() ) {
                    curDepth++;
                    nd.getNodeFor(a).accept(this);
                    curDepth--;
                }
                if ( nd.getElseNode() != null ) {
                    curDepth++;
                    nd.getElseNode().accept(this);
                    curDepth--;
                }
            }

            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
                for ( Answer a : nd.getAnswers() ) {
                    curDepth++;
                    nd.getNodeFor(a).accept(this);
                    curDepth--;
                }
            }

            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                if ( shouldLinkTo(nd.getNextNode()) ) {
                    curDepth++;
                    nd.getNextNode().accept(this);
                    curDepth--;
                } else {
                    if (curDepth>maxDepth.get()) updateMax(nd);
                }
            }

            @Override
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                if ( shouldLinkTo(nd.getNextNode()) ) {
                    curDepth++;
                    nd.getNextNode().accept(this);
                    curDepth--;
                } else {
                    if (curDepth>maxDepth.get()) updateMax(nd);
                }
            }

            @Override
            public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
            }

            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                if ( shouldLinkTo(nd.getNextNode()) ) {
                    curDepth++;
                    nd.getNextNode().accept(this);
                    curDepth--;
                } else {
                    if (curDepth>maxDepth.get()) updateMax(nd);
                }
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                if ( shouldLinkTo(nd.getNextNode()) ) {
                    curDepth++;
                    nd.getNextNode().accept(this);
                    curDepth--;
                } else {
                    if (curDepth>maxDepth.get()) updateMax(nd);
                }
            }

            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
            }

            @Override
            public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {
                if (curDepth>maxDepth.get()) updateMax(nd);
            }
            
            private void updateMax( Node newMax ) {
                maxDepth.set(curDepth);
                aDeepest[0] = newMax;
            }
        });
        
        return aDeepest[0];
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
        return s.replaceAll("\\\\", "\\\\\\\\").replaceAll("\\[", "").replaceAll("]","/");
    }
    
    GvEdge makeEdge( Node from, Node to ) {
        return makeEdge(sanitizeId(from.getId()),to);
    }
    
    GvEdge makeEdge( String fromGvNodeId, Node to ) {
        Node arrowDest = getFirstNonContainerNode(to);
        GvEdge edge = edge(fromGvNodeId, nodeId(arrowDest));
        if ( to instanceof ContainerNode ) {
            edge = edge.add("lhead", "cluster_"+nodeId(((ContainerNode)to).getStartNode()));
        }
        
        return edge;
    }
    
    protected String makeSameRank( Set<Node> nodes ) {
        return nodes.stream()
                    .map( nd->getFirstNonContainerNode(nd))
                    .map( nd->sanitizeId(nd.getId()) )
                    .collect( joining(",", "{rank=same ", "}"));
    }
    
    protected Node getFirstNonContainerNode( Node nd ) {
        while ( nd instanceof ContainerNode ) {
            nd = ((ContainerNode)nd).getStartNode();
        }
        return nd;
    }
    
    protected boolean shouldLinkTo( Node nd ) {
        return drawEndNodes || (! (nd instanceof EndNode || nd instanceof ContinueNode)) ;
    }

    public void setDrawEndNodes(boolean drawEndNodes) {
        this.drawEndNodes = drawEndNodes;
    }

}
