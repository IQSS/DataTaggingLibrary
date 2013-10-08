package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.tags.DataTags;

/**
 * An atomic part of the program - the equivalent of a line of code 
 * in a regular program.
 * 
 * @author michael
 */
public class Node {
	
	private final String id;
	private String title;
	private String text;
	private FlowChart chart;

	public Node(String id) {
		this( id, null );
	}

	public Node(String id, String title) {
		this( id, title, null, null );
	}

	public Node(String id, String title, String text, FlowChart chart) {
		this.id = id;
		this.title = title;
		this.text = text;
		this.chart = chart;
	}

	public String getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public FlowChart getChart() {
		return chart;
	}

	public void setChart(FlowChart chart) {
		this.chart = chart;
	}
	 
	public DataTags absoluteTags() {
		return null; // TODO implement.
	}
	
}
