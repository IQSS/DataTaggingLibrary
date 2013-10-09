package edu.harvard.iq.datatags.runtime;

/**
 * An atomic part of the program - the equivalent of a line of code 
 * in a regular program.
 * 
 * @author michael
 */
public class Node extends RuntimeEntity {
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

	public FlowChart getChart() {
		return chart;
	}

	public void setChart(FlowChart chart) {
		this.chart = chart;
	}
}
