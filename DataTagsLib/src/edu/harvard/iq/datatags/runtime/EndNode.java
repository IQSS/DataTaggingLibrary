package edu.harvard.iq.datatags.runtime;

/**
 * A node that terminates the run, if and when the execution gets to it.
 * @author michael
 */
public class EndNode extends Node {

	public EndNode(String id) {
		super(id);
	}

	public EndNode(String id, String title) {
		super(id, title);
	}
	
	public EndNode(String id, String title, String text, FlowChart chart) {
		super(id, title, text, chart);
	}
	
}
