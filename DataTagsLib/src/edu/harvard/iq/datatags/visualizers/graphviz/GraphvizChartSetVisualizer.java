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
import static edu.harvard.iq.datatags.model.values.Answer.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
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
				edges.add( edgeStr(nd, nodeId(nd.getNodeFor(ans)), ans.getAnswerText(), "") );
				
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
			edges.add( edgeStr(nd, nodeId(nd.getNextNode()), "next", ""));
			return null;
		}
		
		@Override
		public Void visitTodoNode(TodoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label("todo\\n"+node.getTodoText()).gv() );
			
			edges.add( edgeStr(node, nodeId(node.getNextNode()), "next", ""));
			return null;
		}
		
		@Override
		public Void visitSetNode(SetNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.fillColor("#AADDAA")
						.shape(GvNode.Shape.rect)
						.label( ">"+nodeId(nd) + "<\\nSet")
						.gv());
			edges.add( edgeStr(nd, nodeId(nd.getNextNode()), "next", ""));
			return null;
		}

		@Override
		public Void visitEndNode(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( nodeStr(nd, "shape=\"point\" width=\"0.25\" fillcolor=\"#333333\" height=\"0.25\"") );
			return null;
		}
		
		private String nodeStr( Node n, String extras ) {
			extras = extras.trim();
			if ( ! extras.isEmpty() ) {
				extras = " " + extras + " ";
			}
			
			return String.format( "%s [label=\"%s\" id=\"%s\"" + extras + "]", 
					nodeId(n), humanTitle(n), nodeId(n) );
		}
		
		private String edgeStr( Node n, String dest, String title, String extras ) {
			extras = extras.trim();
			if ( ! extras.isEmpty() ) {
				extras = " " + extras + " ";
			}
			
			return String.format( "%s -> %s [label=\"%s\"" + extras + "]", 
					nodeId(n), dest, title);
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
		
		wrt.write( nodeId(fc.getStart()) +"[peripheries=2]" );
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
		return sanitizeId(chartId) + "__" + sanitizeId( nodeId );
	}
	
	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}
	
}
