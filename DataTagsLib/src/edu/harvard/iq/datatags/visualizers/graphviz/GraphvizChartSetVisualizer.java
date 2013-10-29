package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.runtime.CallNode;
import edu.harvard.iq.datatags.runtime.DecisionNode;
import edu.harvard.iq.datatags.runtime.EndNode;
import edu.harvard.iq.datatags.runtime.FlowChart;
import edu.harvard.iq.datatags.runtime.FlowChartSet;
import edu.harvard.iq.datatags.runtime.Node;
import edu.harvard.iq.datatags.runtime.RuntimeEntity;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

/**
 * Given a {@link FlowChartSet}, instances of this class create gravphviz files
 * visualizing the chartset flow.
 * 
 * @author michael
 */
public class GraphvizChartSetVisualizer {
	
	private FlowChartSet chartSet;
	private final Pattern whitespace = Pattern.compile("\\s|-");
	
	private class NodePainter implements  Node.Visitor<Void> {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();

		public void reset() {
			nodes.clear();
			edges.clear();
		}
		
		@Override
		public Void visitDecisionNode(DecisionNode nd) throws DataTagsRuntimeException {
			nodes.add( nodeStr(nd, "shape=\"oval\"") );
			edges.add( edgeStr(nd, nodeId(nd.getNodeFor(Answer.YES)), "Y", "color=\"green\""));
			edges.add( edgeStr(nd, nodeId(nd.getNodeFor(Answer.NO)), "N", "color=\"red\""));
			return null;
		}

		@Override
		public Void visitCallNode(CallNode nd) throws DataTagsRuntimeException {
			nodes.add( nodeStr(nd, "shape=\"diamond\"") );
			edges.add( edgeStr(nd, nodeId(nd.getCalleeChartId(), nd.getCalleeNodeId()), "call", "color=\"#0000FF\" style=\"dashed\""));
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
	
	/**
	 * Convenience method to write to a file.
	 * @param outputFile
	 * @throws IOException 
	 */
	public void vizualize( Path outputFile ) throws IOException {
		
		try( BufferedWriter out = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8) ) {
			visualize( out );
		}		
	}
	
	/**
	 * Writes the representation to the output stream
	 * @param out
	 * @throws IOException 
	 */
	public void visualize( Writer out ) throws IOException {
		BufferedWriter bOut = ( out instanceof BufferedWriter ) ?
											(BufferedWriter)out : 
											new BufferedWriter(out);
		printHeader( bOut );
		for ( FlowChart fc : chartSet.charts() ) {
			printChart( fc, bOut );
		}
		for ( String s : nodePainter.edges ) {
			bOut.write(s);
			bOut.newLine();
		}
		printFooter( bOut );
		
		bOut.flush();
	}
	
	void printChart( FlowChart fc, BufferedWriter wrt ) throws IOException {
		wrt.write( "subgraph cluster_" + sanitize(fc.getId()) + " {");
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
	
	void printHeader( BufferedWriter out ) throws IOException {
		out.write( "digraph ChartSet {");
		out.newLine();
		out.write( "edge [fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write( "node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write( "rankdir=LR");
		out.newLine();
	}
	
	void printFooter( BufferedWriter out ) throws IOException {
		out.write( "}");
		out.newLine();
	}

	String nodeId( Node nd ) {
		return nodeId( nd.getChart().getId(), nd.getId() );
	}
	
	String nodeId( String chartId, String nodeId ) {
		return sanitize(chartId) + "__" + sanitize( nodeId );
	}
	
	private String humanTitle( RuntimeEntity ent ) {
		return (ent.getTitle() != null ) ?
					String.format("%s: %s", ent.getId(), ent.getTitle() )
					: ent.getId();
	}
	
	String sanitize( String s ) {
		return whitespace.matcher(s.trim()).replaceAll("_");
	}
	
	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}
	
}
