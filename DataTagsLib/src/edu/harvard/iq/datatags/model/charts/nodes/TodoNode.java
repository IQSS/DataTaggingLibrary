package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that marks a section of the chart that will be implemented later.
 * @author michael
 */
public class TodoNode extends ThroughNode {
	
	private String todoText;
	
	public TodoNode(String id) {
		super(id);
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit( this );
	}

	public String getTodoText() {
		return todoText;
	}

	public void setTodoText(String todoText) {
		this.todoText = todoText;
	}
}
