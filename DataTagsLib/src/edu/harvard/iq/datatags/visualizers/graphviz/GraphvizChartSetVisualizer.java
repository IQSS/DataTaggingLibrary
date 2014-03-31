package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;

/**
 * Given a {@link FlowChartSet}, instances of this class create gravphviz files
 * visualizing the chartset flow.
 * 
 * @author michael
 */
public class GraphvizChartSetVisualizer extends GraphvizVisualizer {
	
	private FlowChartSet chartSet;

	private class NodePainter implements  Node.Visitor<Void> {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();

		public void reset() {
			nodes.clear();
			edges.clear();
		}
		
		@Override
		public Void visitAskNode(AskNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
					.shape(GvNode.Shape.oval)
					.label( ">" + nodeId(nd) + "< | ask\\n" + nd.getText() )
					.gv());
			for ( Answer ans : nd.getAnswers() ) {
				edges.add( edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).label(ans.getAnswerText()).gv() );
				
			}

			return null;
		}

		@Override
		public Void visitCallNode(CallNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( nd.getCalleeChartId() +"/" + nd.getCalleeNodeId())
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
			edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).label("next").gv() );
			return null;
		}
		
		@Override
		public Void visitTodoNode(TodoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label("todo\\n"+node.getTodoText()).gv() );
			
            edges.add( edge(nodeId(node), nodeId(node.getNextNode())).label("next").gv() );
			return null;
		}
		
		@Override
		public Void visitSetNode(SetNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.fillColor("#AADDAA")
						.shape(GvNode.Shape.rect)
						.label( ">"+nodeId(nd) + "<\\nSet")
						.gv());
            edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).label("next").gv() );
            
			return null;
		}

		@Override
		public Void visitEndNode(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd)).shape(GvNode.Shape.point)
                            .fillColor("#333333").add("height", "0.25").add("width", "0.25").gv() );
			return null;
		}
		
	}
	
	private final NodePainter nodePainter = new NodePainter();
	
	@Override
	protected void printBody(BufferedWriter out) throws IOException {
		for (FlowChart fc : chartSet.charts()) {
			printChart(fc, out);
		}
		for (String s : nodePainter.edges) {
			out.write(s);
			out.newLine();
		}
	}
	
	void printChart( FlowChart fc, BufferedWriter wrt ) throws IOException {
		wrt.write( "subgraph cluster_" + sanitizeId(fc.getId()) + " {");
		wrt.newLine();

		wrt.write( String.format("label=\"%s\"", humanTitle(fc)) );
		wrt.newLine();
		
		wrt.write( node(nodeId(fc.getStart())).peripheries(2).gv() );
		wrt.newLine();
		
		nodePainter.nodes.clear();
		for ( Node n : fc.nodes() ) {
			try {
				n.accept( nodePainter );
			} catch (DataTagsRuntimeException ex) {
				Logger.getLogger(GraphvizChartSetVisualizer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		for ( String s : nodePainter.nodes ) {
			wrt.write(s);
			wrt.newLine();
		}
		
		wrt.write("}");
		wrt.newLine();
		
	}

	String nodeId( Node nd ) {
		return nodeId( nd.getChart() != null ? nd.getChart().getId() : "chart_is_null", nd.getId() );
	}
	
	String nodeId( String chartId, String nodeId ) {
		return chartId + "#" + nodeId;
	}
	
	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}
	
}
