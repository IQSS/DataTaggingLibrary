package edu.harvard.iq.datatags.runtime;

/**
 * An atomic part of the program - the equivalent of a line of code 
 * in a regular program.
 * 
 * @author michael
 */
public abstract class Node extends RuntimeEntity {
	
	public interface Visitor<R> {
		R visitDecisionNode( DecisionNode nd );
		R visitCallNode( CallNode nd );
		R visitEndNode( EndNode nd );
	}
	
	private FlowChart chart;

	public Node(String id) {
		this( id, null );
	}

	public Node(String id, String title) {
		this( id, title, null, null );
	}

	public Node(String id, String title, String text, FlowChart chart) {
		super(id);
		this.title = title;
		this.info = text;
		this.chart = chart;
	}

	public abstract <R> R accept( Node.Visitor<R> vr );
	
	public FlowChart getChart() {
		return chart;
	}

	public void setChart(FlowChart chart) {
		this.chart = chart;
	}
	
}
