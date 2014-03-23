package edu.harvard.iq.datatags.model.charts;

import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.Answer;
import static edu.harvard.iq.datatags.model.values.Answer.NO;
import static edu.harvard.iq.datatags.model.values.Answer.YES;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single flow chart that a users walks through.
 * 
 * @author michael
 */
public class FlowChart extends ChartEntity {
	
	private static final AtomicInteger INDEX = new AtomicInteger(0);
	
	private URL source;
	private Node start;
	private final Map<String, Node> nodes = new TreeMap<>();
	private final EndNode endNode;
	
	private final Node.Visitor<Node> nodeAdder = new Node.Visitor<Node>() {

		@Override
		public Node visitAskNode(AskNode nd) throws DataTagsRuntimeException {
			for ( Answer ans : nd.getAnswers() ) {
				nd.getNodeFor(ans).accept(this);
			}
			return visitSimpleNode(nd);
		}

		@Override
		public Node visitSetNode(SetNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visitCallNode(CallNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visitTodoNode(TodoNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visitEndNode(EndNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}
		
		Node visitSimpleNode( Node n ) {
			n.setChart(FlowChart.this);
			nodes.put( n.getId(), n );
			return n;
		}
	};
	
	public FlowChart() {
		this( "FlowChart-"+INDEX.incrementAndGet());
	}
	
	public FlowChart(String anId) {
		super(anId);
		endNode = new EndNode(anId + "-end", "");
		nodes.put( endNode.getId(), endNode);
	}

	public URL getSource() {
		return source;
	}

	public void setSource(URL source) {
		this.source = source;
	}

	public Node getStart() {
		return start;
	}

	public void setStart(Node start) {
		this.start = start;
	}
	
	public Node getNode( String nodeId ) {
		return nodes.get(nodeId);
	}
	
	/**
	 * Adds the node, and its descendents, to the chart.
	 * @param <T> the static type of the node
	 * @param n the node
	 * @return the node, for call chaining.
	 */
	public <T extends Node> T add( T n ) {
		n.accept( nodeAdder );
		return n;
	}

	public Iterable<Node> nodes() {
		return nodes.values();
	}

	public EndNode getEndNode() {
		return endNode;
	}
	
	@Deprecated // As part of the unrestricted answer design, this should be done by the parser.
	public void connectOpenEnds( final Node defaultNode ) {
		Node.Visitor connector = new Node.Visitor<Void>(){

			@Override
			public Void visitAskNode(AskNode nd) throws DataTagsRuntimeException {
				for ( Answer a : Arrays.asList( YES, NO) ) {
					if ( nd.getNodeFor(a) == null ) {
						nd.setNodeFor(a, defaultNode);
					}
				}
				return null;
			}

			@Override
			public Void visitCallNode(CallNode nd) throws DataTagsRuntimeException {
				if (nd.getNextNode() == null ) nd.setNextNode(defaultNode);
				return null;
			}
			
			@Override
			public Void visitTodoNode(TodoNode nd) throws DataTagsRuntimeException {
				if (nd.getNextNode() == null ) nd.setNextNode(defaultNode);
				return null;
			}
			
			@Override
			public Void visitSetNode(SetNode nd) throws DataTagsRuntimeException {
				if (nd.getNextNode() == null ) nd.setNextNode(defaultNode);
				return null;
			}

			@Override
			public Void visitEndNode(EndNode nd) throws DataTagsRuntimeException {return null;}
		};
		for ( Node n : nodes() ) n.accept(connector);
	}
}
