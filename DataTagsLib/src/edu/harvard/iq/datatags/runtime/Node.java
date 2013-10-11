package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * An atomic part of the program - the equivalent of a line of code 
 * in a regular program.
 * 
 * @author michael
 */
public abstract class Node extends RuntimeEntity {
	
	public interface Visitor<R> {
		R visitDecisionNode( DecisionNode nd ) throws DataTagsRuntimeException;
		R visitCallNode( CallNode nd ) throws DataTagsRuntimeException;
		R visitEndNode( EndNode nd ) throws DataTagsRuntimeException;
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

	public abstract <R> R accept( Node.Visitor<R> vr ) throws DataTagsRuntimeException ;
	
	public FlowChart getChart() {
		return chart;
	}

	public void setChart(FlowChart chart) {
		this.chart = chart;
	}
	
}
