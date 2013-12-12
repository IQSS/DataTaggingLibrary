package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that marks a section of the chart that will be implemented later.
 * @author michael
 */
public class TodoNode extends ThroughNode {

	public TodoNode(String id) {
		super(id);
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visitTodoNode( this );
	}
	
}
