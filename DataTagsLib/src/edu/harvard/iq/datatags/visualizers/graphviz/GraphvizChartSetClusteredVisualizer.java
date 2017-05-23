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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Given a {@link DecisionGraph}, instances of this class create gravphviz files
 * visualizing the decision graph flow.
 * 
 * @author michael
 */
public class GraphvizChartSetClusteredVisualizer extends AbstractGraphvizDecisionGraphVisualizer {
    
            
	private class NodePainter extends  Node.VoidVisitor {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();
        Set<Node> targets = new HashSet<>();
        
		public void reset() {
			nodes.clear();
			edges.clear();
		}
        
        @Override
        public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {

            nodes.add(node(nodeId(nd))
                    .shape(GvNode.Shape.egg)
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

                edges.add(edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).tailLabel(label.toString()).gv());
                targets.add(nd.getNodeFor(ans));
            }
            edges.add(edge(nodeId(nd), nodeId(nd.getElseNode())).tailLabel("else").gv());
            targets.add(nd.getElseNode());
        }
		
		@Override
		public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if ( nodeText.length() > 140 ) {
                nodeText = nodeText.substring(0,140) + "...";
            }
			nodes.add( node(nodeId(nd))
					.shape(GvNode.Shape.oval)
					.label( idLabel(nd) + "ask\n" + wrap(nodeText) )
					.gv());
			for ( Answer ans : nd.getAnswers() ) {
				edges.add( edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).tailLabel(ans.getAnswerText()).gv() );
                targets.add( nd.getNodeFor(ans));
			}
		}

		@Override
		public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + nd.getCalleeNodeId())
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
			edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
		}
		
        @Override
		public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + "REJECT\n" + wrap(nd.getReason())  )
						.shape(GvNode.Shape.hexagon)
						.fillColor("#FFAAAA")
						.gv() );
		}
		
		@Override
		public void visitImpl(ToDoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label(idLabel(node) + "todo\n"+ wrap(node.getTodoText())).gv() );
			
            edges.add( edge(nodeId(node), nodeId(node.getNextNode())).gv() );
            targets.add( node.getNextNode() );
		}
		
		@Override
		public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) )
                    .append("Set\n");
            for ( SlotType tt : nd.getTags().getNonEmptySubSlotTypes() ) {
                label.append( tt.getName() )
                        .append( "=" )
                        .append( nd.getTags().get(tt).accept(valueNamer) )
                        .append("\n");
            }
			nodes.add( node(nodeId(nd))
						.fillColor("#AADDAA")
						.shape(GvNode.Shape.rect)
						.label( label.toString() )
						.gv());
            edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
		}

		@Override
		public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
                        .shape(GvNode.Shape.point)
                        .fontColor("#AAAAAA")
                        .fillColor("#000000")
                        .add("height", "0.2")
                        .add("width", "0.2")
                        .label("x").gv() );
		}
                
                @Override
                public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
                    
                }
		
        private String idLabel( Node nd ) {
            return nd.getId().startsWith("[#") ? "" : nd.getId()+"\\n";
        }
	}
	
    
    @Override
    void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph " + getDecisionGraphName() + " {");
		out.newLine();
        out.write( "fontname=\"Courier\"" );
		out.newLine();
		out.write("edge [fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write("node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
        out.write( node("start")
                .fillColor("transparent")
                .shape(GvNode.Shape.none)
                .fontColor("#008800")
                .fontSize(16)
                .gv() );
		out.newLine();
	}
    
	@Override
	protected void printBody(BufferedWriter out) throws IOException {
        printChart(theGraph, out);
        out.write( edge("start", nodeId(theGraph.getStart()))
                    .color("#008800")
                    .penwidth(4)
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
 