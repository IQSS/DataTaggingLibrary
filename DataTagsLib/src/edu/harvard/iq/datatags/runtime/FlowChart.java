package edu.harvard.iq.datatags.runtime;

import java.net.URL;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single flow chart that a users walks through.
 * 
 * @author michael
 */
public class FlowChart extends RuntimeEntity {
	
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
	
}
