package edu.harvard.iq.policymodels.visualizers.graphviz;

import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ContainerNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.EndNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.PartNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.SectionNode;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvNode.node;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * A decision graph visualizer that draws sections and closed nodes, and then
 * details them below.
 * 
 * @author michael
 */
public class ClosedSectionGraphVizualizer extends GraphvizDecisionGraphClusteredVisualizer {
    
    protected final Set<SectionNode> sectionsToDraw = new HashSet<>();
    final Map<Node, Set<SectionNode>> dependencies = new HashMap<>(); 
    final Node[] curTraversed = new Node[1];
        
    public ClosedSectionGraphVizualizer() {
        setConcentrate(false);
    }
    
    class ClosedSectionNP extends GraphvizDecisionGraphClusteredVisualizer.NodePainter {
        @Override
        public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
            
            sectionsToDraw.add(nd);
            dependencies.get(curTraversed[0]).add(nd);
            
            out.println(node(nodeId(nd))
                    .fillColor(COL_SECTION)
                    .shape(GvNode.Shape.folder)
                    .label("Section\n" + nd.getTitle() )
                    .gv());
            
            if ( shouldLinkTo(nd.getNextNode()) ) {
                advanceTo(nd.getNextNode());
                out.println(makeEdge(nd, nd.getNextNode()).gv());
            }
        }
        
        @Override
        public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
            String effTitle =  nd.getId();
            if ( nd.getTitle()!=null && (!nd.getTitle().trim().isEmpty()) ) {
                effTitle += "\\n" + nd.getTitle().trim();
            }
            out.println( node(sanitizeId(nd.getId()+"__PART_START")).hidden().gv() );
            out.println("subgraph cluster_" + nodeId(nd)  + "{ ");
            out.println("label=\"Part " + effTitle +  "\"");
            out.println("color=\"#AAAAAA\"");
            
            String arrowDestId;
            if ( nd.getStartNode() instanceof EndNode ) {
                // edge case: part is empty
                arrowDestId = nodeId(nd)+"__EMPTY";
                out.println( node(arrowDestId)
                               .shape(GvNode.Shape.diamond)
                               .fillColor("#BBBBBB")
                               .fontColor("#888888")
                               .color("#888888")
                               .label("(empty)")
                               .gv());
            } else {
                advanceTo(nd.getStartNode());
                arrowDestId = nodeId(nd.getStartNode());
            }
            
            out.println("}");
            GvEdge edge = edge(sanitizeId(nd.getId()+"__PART_START"), arrowDestId);
            
            out.println(edge.gv());
            
        }
    }
    
    @Override
    void printChart(DecisionGraph dg, PrintWriter wrt) throws IOException {
        wrt.println("subgraph cluster_" + sanitizeId(dg.getId()) + " {");
        wrt.println(String.format("label=\"%s\"", humanTitle(dg)));
        
        // group to subcharts
        Set<Node> subchartHeads = findSubchartHeades(dg);
        
        NodePainter np = new ClosedSectionNP();
        np.out = wrt;
        
        subchartHeads.forEach(chartHead -> {
            curTraversed[0] = chartHead;
            dependencies.put( curTraversed[0], new HashSet<>());
            chartHead.accept(np);
        });
        
        
        // print sections
        Set<SectionNode> drawnSections = new HashSet<>();
        while ( !sectionsToDraw.isEmpty() ) {
            SectionNode sectionNode = sectionsToDraw.iterator().next();
            sectionsToDraw.remove(sectionNode);
            drawnSections.add(sectionNode);
            curTraversed[0] = sectionNode.getStartNode();
            dependencies.put( curTraversed[0], new HashSet<>());
            
            wrt.println("subgraph cluster_" + nodeId(sectionNode) + " {");
            wrt.println(String.format("label=\"%s\"", sanitizeTitle(sectionNode.getTitle())) );
            wrt.println("color=\""+COL_SUBGRAPH_EDGE+"\"");
            sectionNode.getStartNode().accept(np);
            wrt.println("}");
        };
       
        // link section nodes to section details
        drawnSections.forEach( sectionNode-> {
            wrt.println(edge(nodeId(sectionNode), nodeId(sectionNode.getStartNode()))
                               .style(GvEdge.Style.Dashed )
                               .constraint(false)
                               .penwidth(3)
                               .color( COL_SECTION )
                               .gv());
        });
        
        wrt.println("edge [style=invis]");
        dependencies.entrySet().stream()
            .filter(kv->!kv.getValue().isEmpty())
            .forEach( kv -> {
                Set<SectionNode> dependants = kv.getValue();
                Node traversalStart = kv.getKey();
                if ( traversalStart instanceof PartNode ) {
                    traversalStart = ((PartNode)traversalStart).getStartNode();
                }
                Node subgraphBottom = findDeepestDrawnNode(traversalStart);
                dependants.forEach( dep -> {
                    wrt.println( edge(nodeId(subgraphBottom), nodeId(dep.getStartNode())).gv() );
                });
            });
        
        if ( isDrawCallLinks() ) {
            drawCallLinks(wrt);
        }
        
        wrt.println("}");

    }
    
    @Override
    GvEdge makeEdge( String fromGvNodeId, Node to ) {
        GvEdge edge;
        if ( to instanceof PartNode ) {
            Node arrowDest = getFirstNonContainerNode(to);
            edge = edge(fromGvNodeId, nodeId(arrowDest));
            edge = edge.add("lhead", "cluster_"+nodeId(((ContainerNode)to).getStartNode()));
        } else {
            edge = edge(fromGvNodeId, nodeId(to));
        }
        
        return edge;
    }
    
}   
