package edu.harvard.iq.datatags.runtime;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;

/**
 * A single flow chart that a users walks through.
 * 
 * @author michael
 */
public class FlowChart extends RuntimeEntity {
	
	private URL source;
	private Node start;
	private final Map<String, Node> nodes = new TreeMap<>();

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
	
	public void addNode( Node n ) {
		n.setChart(this);
		nodes.put( n.getId(), n );
	}
	
}
