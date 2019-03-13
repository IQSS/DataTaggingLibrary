package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ContainerNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ContinueNode;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Set;

/**
 * Given a {@link DecisionGraph}, instances of this class create gravphviz files
 * visualizing the decision graph flow.
 *
 * @author michael
 */
public class GraphvizDecisionGraphClusteredVisualizer extends AbstractGraphvizDecisionGraphVisualizer {
    
    private boolean drawCallLinks = false;
    private boolean drawEndNodes = false;
    
    private class NodePainter extends AbstractGraphvizDecisionGraphVisualizer.AbstractNodePainter {
        
        @Override
        public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
            out.println(node(nodeId(nd))
                    .shape(GvNode.Shape.egg)
                    .label(idLabel(nd) + "consider\n")
                    .gv());
            nd.getAnswers().forEach( option -> {
                StringBuilder label = new StringBuilder();
                option.getNonEmptySubSlots().forEach( tt -> {
                    label.append(tt.getName())
                            .append("=")
                            .append(option.get(tt).accept(valueNamer))
                            .append("\n");
                });
                advanceTo(nd.getNodeFor(option));
                out.println(makeEdge(nd, nd.getNodeFor(option)).tailLabel(label.toString()).gv());
            });
            
            if ( nd.getElseNode() != null ) {
                advanceTo(nd.getElseNode());
                out.println(makeEdge(nd, nd.getElseNode()).tailLabel("else").gv());
            }
        }

        @Override
        public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if (nodeText.length() > 140) {
                nodeText = nodeText.substring(0, 140) + "...";
            }
            out.println(node(nodeId(nd))
                    .shape(GvNode.Shape.oval)
                    .label(idLabel(nd) + "ask\n" + wrap(nodeText))
                    .gv());
            nd.getAnswers().forEach( ans -> {
                advanceTo(nd.getNodeFor(ans));
                out.println(makeEdge(nd, nd.getNodeFor(ans)).tailLabel(ans.getAnswerText()).gv());
            });
        }

        @Override
        public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
            out.println(node(nodeId(nd))
                    .label(idLabel(nd) + sanitizeIdDisplay(nd.getCalleeNode().getId()))
                    .shape(GvNode.Shape.cds)
                    .fillColor("#BBBBFF")
                    .gv());
            advanceTo(nd.getNextNode());
            if ( shouldLinkTo(nd.getNextNode()) ) {
                out.println(makeEdge(nd, nd.getNextNode()).gv());
            }
        }

        @Override
        public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
            out.println(node(nodeId(nd))
                    .label(idLabel(nd) + "REJECT\n" + wrap(nd.getReason()))
                    .shape(GvNode.Shape.hexagon)
                    .fillColor("#FFAAAA")
                    .gv());
        }

        @Override
        public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
            out.println(node(nodeId(nd))
                    .fillColor("#AAFFAA")
                    .shape(GvNode.Shape.note)
                    .label(idLabel(nd) + "todo\n" + wrap(nd.getTodoText())).gv());
            advanceTo(nd.getNextNode());
            if ( shouldLinkTo(nd.getNextNode()) ) {
                out.println(makeEdge(nd, nd.getNextNode()).gv());
            }
        }

        @Override
        public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append(idLabel(nd))
                    .append("Set\n");
            nd.getTags().getNonEmptySubSlots().forEach( tt -> {
                label.append(tt.getName())
                        .append("=")
                        .append(nd.getTags().get(tt).accept(valueNamer))
                        .append("\n");
            });
            out.println(node(nodeId(nd))
                    .fillColor("#AADDAA")
                    .shape(GvNode.Shape.rect)
                    .label(label.toString())
                    .gv());
            advanceTo(nd.getNextNode());
            if ( shouldLinkTo(nd.getNextNode()) ) {
                out.println(makeEdge(nd, nd.getNextNode()).gv());
            }
        }

        @Override
        public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            if ( drawEndNodes ) {
                out.println(node(nodeId(nd))
                    .shape(GvNode.Shape.point)
                    .fontColor("#AAAAAA")
                    .fillColor("#000000")
                    .add("height", "0.2")
                    .add("width", "0.2")
                    .label("x").gv());
            }
        }
        
        @Override
        public void visitImpl(ContinueNode nd) throws DataTagsRuntimeException {
            out.println(node(nodeId(nd))
                    .shape(GvNode.Shape.point)
                    .fontColor("#AAAAAA")
                    .fillColor("#8833AA")
                    .add("height", "0.2")
                    .add("width", "0.2")
                    .label("c").gv());
        }
        
        @Override
        public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
            out.println("subgraph cluster_" + nodeId(nd)  + "{ ");
            out.println("label=\"Section " + nd.getTitle() + "\"");
            out.println( node(sanitizeId(nd.getId()+"__[scstart]")).hidden().gv() );
            advanceTo(nd.getStartNode());
            out.println("}");
            out.println( edge(sanitizeId(nd.getId()+"__[scstart]"), nodeId(nd.getStartNode())).gv() );
                        
            advanceTo(nd.getNextNode());
            if ( shouldLinkTo(nd.getNextNode()) ) {
                out.println(makeEdge(nd.getStartNode(), nd.getNextNode())
                            .add("ltail", "cluster_" + nodeId(nd)).gv() );
            }
        }

        @Override
        public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
            String effTitle =  nd.getId();
            if ( nd.getTitle()!=null && (!nd.getTitle().trim().isEmpty()) ) {
                effTitle += "\\n" + nd.getTitle().trim();
            }
            out.println( node(sanitizeId(nd.getId()+"__[pstart]")).hidden().gv() );
            out.println("subgraph cluster_" + nodeId(nd)  + "{ ");
            out.println("label=\"Part " + effTitle +  "\"");
            advanceTo(nd.getStartNode());
            out.println("}");
            Node arrowDest = getFirstNonContainerNode(nd);
            GvEdge edge = edge(sanitizeId(nd.getId()+"__[pstart]"), nodeId(arrowDest));
            if ( nd.getStartNode() instanceof ContainerNode ) {
                edge = edge.add("lhead", "cluster_"+nodeId(nd.getStartNode()));
            }
            out.println(edge.gv() );
        }

    }

    @Override
    void printHeader(PrintWriter bOut) throws IOException {
        PrintWriter out = new PrintWriter(bOut, true);
        out.println("digraph decisionGraph {");
        out.println("graph [fontname=\"Courier\" concentrate=true compound=true]");
        out.println("edge [fontname=\"Helvetica\" fontsize=\"10\"]");
        out.println("node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
        out.println(node(START_NODE_NAME)
                .label("start")
                .fillColor("transparent")
                .shape(GvNode.Shape.none)
                .fontColor("#008800")
                .fontSize(16)
                .gv());
        out.println("{rank=source; " + START_NODE_NAME + "}");
    }

    @Override
    protected void printBody(PrintWriter out) throws IOException {
        printChart(theGraph, new PrintWriter(out, true));
        out.println(edge(START_NODE_NAME, nodeId(theGraph.getStart()))
                .color("#008800")
                .penwidth(4)
                .gv());
    }

    void printChart(DecisionGraph fc, PrintWriter wrt) throws IOException {
        wrt.println("subgraph cluster_" + sanitizeId(fc.getId()) + " {");
        wrt.println(String.format("label=\"%s\"", humanTitle(fc)));

        // group to subcharts
        Set<Node> subchartHeads = findSubchartHeades(fc);
        NodePainter np = new NodePainter();
        np.out = wrt;
        subchartHeads.forEach( chartHead -> {
            chartHead.accept(np);
        });
        
//        wrt.println( makeSameRank(subchartHeads) );
        
        if ( drawCallLinks ) {
            drawCallLinks(wrt);
        }
        
        wrt.println("}");

    }
    
    private boolean shouldLinkTo( Node nd ) {
        return (! (nd instanceof EndNode) ) || drawEndNodes ;
    }

    public void setDrawCallLinks(boolean drawCallLinks) {
        this.drawCallLinks = drawCallLinks;
    }

    public void setDrawEndNodes(boolean drawEndNodes) {
        this.drawEndNodes = drawEndNodes;
    }
    
}
