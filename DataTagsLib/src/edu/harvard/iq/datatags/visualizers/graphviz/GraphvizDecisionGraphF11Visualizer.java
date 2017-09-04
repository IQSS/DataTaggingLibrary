package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Given a {@link DecisionGraph}, instances of this class create gravphviz files
 * visualizing the decision graph flow.
 * 
 * @author michael
 */
public class GraphvizDecisionGraphF11Visualizer extends AbstractGraphvizDecisionGraphVisualizer {
    
    private static final String NODE_FILL_COLOR = "#3B8298";
        
    /** Fill color for the "no" answer node. */
    private static final String NO_NODE_FILL_COLOR = "#515151";
    
    private boolean showEndNodes = false;
    
    public GraphvizDecisionGraphF11Visualizer(boolean showEndNodes) {
        this.showEndNodes = showEndNodes;
    }
    
    public GraphvizDecisionGraphF11Visualizer() {
        this(false);
    }
            
	private class NodePainter extends AbstractGraphvizDecisionGraphVisualizer.AbstracctNodePainter {
		
        @Override
        public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {

            out.println(node(nodeId(nd))
                    .label(idLabel(nd) + "when")
                    .gv());
            int ansNodeCount=0;
            for (ConsiderAnswer ans : nd.getAnswers()) {
                StringBuilder label = new StringBuilder();
                ans.getAnswer().getNonEmptySubSlots().forEach( tt -> 
                    label.append(tt.getName())
                            .append("=")
                            .append(ans.getAnswer().get(tt).accept(valueNamer))
                            .append("\n")
                );
                String ansId = nodeId(nd) + "_" + (++ansNodeCount);
                out.println( considerAnswerNodeGv(ansId, label.toString()) );
                out.println( edge(nodeId(nd), ansId).arrowhead(GvEdge.ArrowType.None).gv() );
                Node nextNode = nd.getNodeFor(ans);
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    advanceTo(nextNode);
                    out.println( edge(ansId, nodeId(nextNode)).gv() );
                }
            }
            if ( nd.getElseNode() != null ) {
                String elseId = nodeId(nd)+"_ELSE";
                out.println( considerAnswerNodeGv(elseId, "else") );
                out.println( edge(nodeId(nd), elseId).arrowhead(GvEdge.ArrowType.None).gv() );
                
                Node nextNode = nd.getElseNode();
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    advanceTo(nextNode);
                    out.println( edge(elseId, nodeId(nextNode)).gv() );
                }
                
            }
        }
		
		@Override
		public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if ( nodeText.length() > 140 ) {
                nodeText = nodeText.substring(0,140) + "...";
            }
			out.println( node(nodeId(nd))
					    .label(idLabel(nd) + wrap(nodeText))
					    .gv());
            
			for ( Answer ans : nd.getAnswers() ) {
                String ansId = nodeId(nd) + "_to_" + nodeId(nd.getNodeFor(ans));
                out.println( answerNodeGv(ansId, wrapAt(ans.getAnswerText(), 35)) );
				out.println( edge(nodeId(nd), ansId).arrowhead(GvEdge.ArrowType.None).gv() );
             
                Node nextNode = nd.getNodeFor(ans);
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    advanceTo(nextNode);
                    out.println( edge(ansId, nodeId(nextNode)).gv() );
                }
			}
		}
        
        private String considerAnswerNodeGv( String nodeId, String ans ) {
            return node(nodeId).label(wrap(ans))
                    .fontColor("white")
                    .fillColor(NO_NODE_FILL_COLOR)
                    .gv();
        }
        
        private String answerNodeGv( String nodeId, String ans ) {
            String answerText = ans.toLowerCase();
            GvNode nodeBld = node(nodeId).label(ans)
                    .shape(GvNode.Shape.circle)
                    .width(0.7);
            switch( answerText ) {
                case "yes":
                    return nodeBld.label("Yes").fontColor(NO_NODE_FILL_COLOR)
                              .fillColor("white").color(NO_NODE_FILL_COLOR)
                                .style(GvNode.Style.dashed).gv();
                case "no":
                    return nodeBld.label("No").fontColor("white").fillColor(NO_NODE_FILL_COLOR).gv();
                default:
                    return nodeBld.fontColor("white").fillColor(NODE_FILL_COLOR).gv();
            }
        }
        
		@Override
		public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
			out.println( node(nodeId(nd))
						.label( idLabel(nd) + sanitizeIdDisplay(nd.getCalleeNode().getId()))
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
            advanceTo(nd.getCalleeNode());
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                advanceTo(nd.getNextNode());
                out.println( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
		}
		
        @Override
		public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
			out.println( node(nodeId(nd))
						.label( idLabel(nd) + wrap(nd.getReason())  )
						.fillColor("#FF4444")
                        .fontColor("#FFFF44")
						.gv() );
		}
		
		@Override
		public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
			out.println( node(nodeId(nd))
							.fillColor("#AAFFAA")
                            .fontColor("#888888")
							.shape(GvNode.Shape.note)
							.label(idLabel(nd) + "todo\n"+ wrap(nd.getTodoText())).gv() );
			if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                advanceTo(nd.getNextNode());
                out.println( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
		}
		
		@Override
		public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) );
            for ( AbstractSlot tt : nd.getTags().getNonEmptySubSlots() ) {
                label.append( tt.getName() )
                        .append( "=" )
                        .append( nd.getTags().get(tt).accept(valueNamer) )
                        .append("\n");
            }
			out.println( node(nodeId(nd))
						.fillColor("#77A576")
						.label( label.toString() )
						.gv());
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                advanceTo(nd.getNextNode());
                out.println( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
		}

		@Override
		public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            if ( showEndNodes ) {
                out.println( node(nodeId(nd))
                            .shape(GvNode.Shape.point)
                            .fontColor("#AAAAAA")
                            .fillColor("#000000")
                            .add("height", "0.2")
                            .add("width", "0.2")
                            .label("x").gv() );
            }
		}
                
        @Override
        public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
            String nodeTitle = nd.getTitle();
            if ( nodeTitle.length() > 140 ) {
                nodeTitle = nodeTitle.substring(0,140) + "...";
            }
            out.println( node(nodeId(nd))
                    .label( idLabel(nd) + wrap(nodeTitle) )
                    .gv());

            out.println("subgraph cluster_section_" + nodeId(nd)  + "{ ");
            out.println("label=\"Section " + nd.getTitle() + "\"");
            advanceTo(nd.getStartNode());
            out.println("}");
            
            out.println(edge(nodeId(nd), nodeId(nd.getStartNode())).gv());
            
            
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                advanceTo(nd.getNextNode());
                out.println(edge(nodeId(nd.getStartNode()), nodeId(nd.getNextNode())).gv()+" [ltail=cluster_section_" + nodeId(nd) + "]");
            }
        }
        
		
	}
	
    @Override
    void printHeader(PrintWriter out) throws IOException {
		out.println("digraph decisionGraph {");
        out.println("fontname=\"Courier\"" );
        out.println("graph[splines=ortho, nodesep=1, concentrate=true compound=true]" );
		out.println("edge [style=dotted arrowhead=open]");
		out.println("node [shape=Mrecord fillcolor=\""+NODE_FILL_COLOR+"\" style=\"filled\" fontcolor=white color=white fontname=\"Helvetica\" fontsize=\"10\"]");
        out.println( node(START_NODE_NAME)
                .label("Start")
                .fillColor("transparent")
                .shape(GvNode.Shape.none)
                .fontColor(NODE_FILL_COLOR)
                .fontSize(12)
                .gv() );
	}
    
	@Override
	protected void printBody(PrintWriter out) throws IOException {
        printChart(theGraph, out);
        out.print( edge(START_NODE_NAME, nodeId(theGraph.getStart()))
                    .color(NODE_FILL_COLOR)
                    .penwidth(1)
                    .style(GvEdge.Style.Solid)
                    .gv());
        out.println("{rank=source; " + START_NODE_NAME + "}");
	}
    
	void printChart( DecisionGraph fc, PrintWriter wrt ) throws IOException {
		wrt.println( "subgraph cluster_" + sanitizeId(fc.getId()) + " {");
		wrt.println( String.format("label=\"%s\"", humanTitle(fc)) );
		
        // group to subcharts
        Set<Node> subchartHeads = findSubchartHeades( fc );
        NodePainter np = new NodePainter();
        np.out = wrt;
        subchartHeads.forEach( chartHead -> {
            wrt.println( "subgraph cluster_" + sanitizeId(chartHead.getId()) + " {");
            wrt.println( String.format("label=\"%s\"; color=\"#AABBDD\"; labeljust=\"l\"", sanitizeTitle(chartHead.getId())) );
            chartHead.accept(np);
            wrt.println("}");
        });
                
        wrt.println( makeSameRank(subchartHeads) );
		wrt.println("}");
		
	}

}
 