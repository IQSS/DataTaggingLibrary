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
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ToDoNode;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.policymodels.util.NodeDepth;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvNode.node;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;
import static java.util.stream.Collectors.joining;

/**
 * A decision graph visualizer that draws sections and closed nodes, and then
 * details them below.
 * 
 * @author michael
 */
public class ClosedSectionGraphVizualizer extends GraphvizDecisionGraphClusteredVisualizer {
    
    protected final Set<SectionNode> sectionsToDraw = new HashSet<>();
    
    class ClosedSectionNP extends GraphvizDecisionGraphClusteredVisualizer.NodePainter {
        @Override
        public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
            
            sectionsToDraw.add(nd);
            
            out.println(node(nodeId(nd))
                    .fillColor("#DDAAAA")
                    .shape(GvNode.Shape.folder)
                    .label("Section\n" + nd.getTitle() )
                    .gv());
            
            if ( shouldLinkTo(nd.getNextNode()) ) {
                advanceTo(nd.getNextNode());
                out.println(makeEdge(nd, nd.getNextNode()).gv());
            }
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
            chartHead.accept(np);
        });
        
        
        // print sections
        Set<SectionNode> drawnSections = new HashSet<>();
        while ( !sectionsToDraw.isEmpty() ) {
            SectionNode sectionNode = sectionsToDraw.iterator().next();
            sectionsToDraw.remove(sectionNode);
            drawnSections.add(sectionNode);
            
            wrt.println("subgraph cluster_" + nodeId(sectionNode) + " {");
            wrt.println(String.format("label=\"%s\"", sanitizeTitle(sectionNode.getTitle())) );
            sectionNode.getStartNode().accept(np);
            wrt.println("}");
        };
       
        // link section nodes to section details
        wrt.println("node [fillcolor=\"transparent\" color=transparent shape=\"point\" fontcolor=\"#008800\" fontsize=\"16\" label=\"\"]");
        drawnSections.forEach( sectionNode-> wrt.println( nodeId(sectionNode)+"__START") );
        
        drawnSections.forEach( sectionNode-> {
                wrt.println(edge(nodeId(sectionNode), nodeId(sectionNode)+"__START")
                               .style(GvEdge.Style.Dashed )
                               .constraint(false)
                               .penwidth(3)
                               .color( "#DDAAAA" )
                               .arrowhead(GvEdge.ArrowType.None)
                               .gv()
                );
                wrt.println(edge(nodeId(sectionNode)+"__START", nodeId(sectionNode.getStartNode()))
                               .style(GvEdge.Style.Dashed )
                               .penwidth(3)
                               .color( "#DDAAAA" )
                               .gv()
                );
        });
        
        if ( ! drawnSections.isEmpty() ) {
            
            Node deepest = findDeepestDrawnNode(dg.getStart());
            wrt.println( edge(nodeId(deepest), nodeId(drawnSections.iterator().next()) + "__START")
                .style(GvEdge.Style.Invis)
                .gv() );

            // put sections below, by making their starts same rank
            wrt.println("{rank=same;" +
                drawnSections.stream().map(n->nodeId(n)+"__START").collect(joining(", "))
                + "}");
        }
        
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
