package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

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
		
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit(this);
	}

	
}
