package edu.harvard.iq.datatags.model.charts;

import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.net.URL;
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

	public FlowChart() {
		this( "FlowChart-"+INDEX.incrementAndGet());
	}
	
	public FlowChart(String anId) {
		super(anId);
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
	
	public <T extends Node> T add( T n ) {
		n.setChart(this);
		nodes.put( n.getId(), n );
		return n;
	}

	public Iterable<Node> nodes() {
		return nodes.values();
	}
	
	public void connectOpenEnds( final Node defaultNode ) {
		Node.Visitor connector = new Node.Visitor<Void>(){

			@Override
			public Void visitAskNode(AskNode nd) throws DataTagsRuntimeException {
				for ( Answer a : Answer.values() ) {
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
