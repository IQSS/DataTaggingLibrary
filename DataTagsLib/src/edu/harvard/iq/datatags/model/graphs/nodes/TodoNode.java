package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that marks a section of the chart that will be implemented later.
 * @author michael
 */
public class TodoNode extends ThroughNode {
	
	private String todoText;
	
	public TodoNode(String id, String aTodoText) {
		super(id);
        todoText = aTodoText;
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.todoText);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof TodoNode) ) {
            return false;
        }
        final TodoNode other = (TodoNode) obj;
        return (Objects.equals(this.todoText, other.todoText))
                ? equalsAsNode(other) : false;
    }
    
    
    
}
