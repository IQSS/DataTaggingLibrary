package edu.harvard.iq.datatags.model.charts;

import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
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
	
    /** Unified end node, as most end nodes are the same. */
    private final EndNode endNode;
	
	private final Node.Visitor<Node> nodeAdder = new Node.Visitor<Node>() {

		@Override
		public Node visit(AskNode nd) throws DataTagsRuntimeException {
			for ( Answer ans : nd.getAnswers() ) {
				if ( nd.getNodeFor(ans)!=null ) {
					nd.getNodeFor(ans).accept(this);
				}
			}
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(SetNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(CallNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(TodoNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(EndNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

        @Override
        public Node visit(RejectNode nd) throws DataTagsRuntimeException {
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
		endNode.setChart(this);
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
    
    public Set<String> nodeIds() {
        return nodes.keySet();
    }

	public EndNode getEndNode() {
		return endNode;
	}

}
