package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ReachableNodesCollector;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Given a {@link DecisionGraph}, instances of this class create gravphviz files
 * visualizing the decision graph flow.
 * 
 * @author michael
 */
public class GraphvizDecisionGraphF11Visualizer extends AbstractGraphvizDecisionGraphVisualizer {
    
    private static final String NODE_FILL_COLOR = "#3B8298";
    private static final String NO_NODE_FILL_COLOR = "#515151";
    
    private boolean showEndNodes = false;
    
    public GraphvizDecisionGraphF11Visualizer(boolean showEndNodes) {
        this.showEndNodes = showEndNodes;
    }
    
    public GraphvizDecisionGraphF11Visualizer() {
        this(false);
    }
            
	private class NodePainter extends  Node.VoidVisitor {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();
        
		public void reset() {
			nodes.clear();
			edges.clear();
		}
        
        @Override
        public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {

            nodes.add(node(nodeId(nd))
                    .label(idLabel(nd) + "consider\n")
                    .gv());
            for (ConsiderAnswer ans : nd.getAnswers()) {
                StringBuilder label = new StringBuilder();
                for (SlotType tt : ans.getAnswer().getNonEmptySubSlotTypes()) {
                    label.append(tt.getName())
                            .append("=")
                            .append(ans.getAnswer().get(tt).accept(valueNamer))
                            .append("\n");
                }
                String ansId = nodeId(nd) + "_" + sanitizeId(ans.getAnswerText());
                nodes.add( answerNodeGv(ansId, ans.getAnswerText()) );
                edges.add( edge(nodeId(nd), ansId).arrowhead(GvEdge.ArrowType.None).gv() );
                Node nextNode = nd.getNodeFor(ans);
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    edges.add( edge(ansId, nodeId(nextNode)).gv() );
                }
            }
            if ( nd.getElseNode() != null ) {
                String elseId = nodeId(nd)+"_ELSE";
                nodes.add( answerNodeGv(elseId, "else") );
                edges.add( edge(nodeId(nd), elseId).arrowhead(GvEdge.ArrowType.None).gv() );
                
                Node nextNode = nd.getElseNode();
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    edges.add( edge(elseId, nodeId(nextNode)).gv() );
                }
                
            }
        }
		
		@Override
		public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if ( nodeText.length() > 140 ) {
                nodeText = nodeText.substring(0,140) + "...";
            }
			nodes.add( node(nodeId(nd))
					.label( idLabel(nd) + wrap(nodeText) )
					.gv());
            
			for ( Answer ans : nd.getAnswers() ) {
                String ansId = nodeId(nd) + "_" + sanitizeId(ans.getAnswerText());
                nodes.add( answerNodeGv(ansId, ans.getAnswerText()) );
				edges.add( edge(nodeId(nd), ansId).arrowhead(GvEdge.ArrowType.None).gv() );
             
                Node nextNode = nd.getNodeFor(ans);
                if ( showEndNodes || !(nextNode instanceof EndNode) ) {
                    edges.add( edge(ansId, nodeId(nextNode)).gv() );
                }
			
			}
		}
        
        
        private String answerNodeGv( String nodeId, String ans ) {
            String answerText = ans.toLowerCase();
            GvNode nodeBld = node(nodeId).label(ans)
                    .shape(GvNode.Shape.circle);
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
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + nd.getCalleeNodeId())
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
		}
		
        @Override
		public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + wrap(nd.getReason())  )
						.fillColor("#FF4444")
                        .fontColor("#FFFF44")
						.gv() );
		}
		
		@Override
		public void visitImpl(ToDoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
                            .fontColor("#888888")
							.shape(GvNode.Shape.note)
							.label(idLabel(node) + "todo\n"+ wrap(node.getTodoText())).gv() );
			if ( showEndNodes || !(node.getNextNode() instanceof EndNode) ) {
                edges.add( edge(nodeId(node), nodeId(node.getNextNode())).gv() );
            }
            
		}
		
		@Override
		public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) );
            for ( SlotType tt : nd.getTags().getNonEmptySubSlotTypes() ) {
                label.append( tt.getName() )
                        .append( "=" )
                        .append( nd.getTags().get(tt).accept(valueNamer) )
                        .append("\n");
            }
			nodes.add( node(nodeId(nd))
						.fillColor("#77A576")
						.label( label.toString() )
						.gv());
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
		}

		@Override
		public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            if ( showEndNodes ) {
                nodes.add( node(nodeId(nd))
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
            nodes.add( node(nodeId(nd))
                    .label( idLabel(nd) + wrap(nodeTitle) )
                    .gv());

            edges.add( edge(nodeId(nd), nodeId(nd.getStartNode())).gv() );
            
            if ( showEndNodes || !(nd.getNextNode() instanceof EndNode) ) {
                edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            }
        }
		
        private String idLabel( Node nd ) {
            return nd.getId().startsWith("[#") ? "" : nd.getId()+"\\n";
        }
	}
	
    @Override
    void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph decisionGraph {");
		out.newLine();
        out.write( "fontname=\"Courier\"" );
		out.newLine();
        out.write( "graph[splines=ortho, nodesep=1, concentrate=true]\n" );
		out.newLine();
		out.write("edge [style=dotted arrowhead=open]");
		out.newLine();
		out.write("node [shape=Mrecord fillcolor=\""+NODE_FILL_COLOR+"\" style=\"filled\" fontcolor=white color=white fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
        out.write( node("start")
                .fillColor("transparent")
                .shape(GvNode.Shape.none)
                .fontColor(NODE_FILL_COLOR)
                .fontSize(12)
                .gv() );
		out.newLine();
	}
    
	@Override
	protected void printBody(BufferedWriter out) throws IOException {
        printChart(theGraph, out);
        out.write( edge("start", nodeId(theGraph.getStart()))
                    .color(NODE_FILL_COLOR)
                    .penwidth(1)
                    .style(GvEdge.Style.Solid)
                    .gv());
        out.write("{rank=source; start}");
		out.newLine();
	}
	
    
    
	void printChart( DecisionGraph fc, BufferedWriter wrt ) throws IOException {
		wrt.write( "subgraph cluster_" + sanitizeId(fc.getId()) + " {");
		wrt.newLine();
		wrt.newLine();
		wrt.write( String.format("label=\"%s\"", humanTitle(fc)) );
		wrt.newLine();
		
        // group to subcharts
        Set<Node> subchartHeads = findSubchartHeades( fc );
        List<String> edges = new LinkedList<>();
        for ( Node chartHead :subchartHeads ) {
            System.out.println("Subchart: " + sanitizeId(chartHead.getId()) );
            wrt.write( "subgraph cluster_" + sanitizeId(chartHead.getId()) + " {");
            wrt.newLine();
            wrt.write( String.format("label=\"%s\"; color=\"#AABBDD\"; labeljust=\"l\"", sanitizeTitle(chartHead.getId())) );
            wrt.newLine();
            
            ReachableNodesCollector rnc = new ReachableNodesCollector();
            chartHead.accept(rnc);
            
            NodePainter np = new NodePainter();
            for ( Node n : rnc.getCollection() ) {
                n.accept(np);
            }
            
            for ( String s : np.nodes ) {
                wrt.write(s);
                wrt.newLine();
            }
            edges.addAll( np.edges );
            wrt.newLine();
            wrt.write("}");
            wrt.newLine();
        }
        
        for ( String s : edges ) {
            wrt.write( s );
            wrt.newLine();
        }
        
		wrt.write("}");
		wrt.newLine();
		
	}

}
 