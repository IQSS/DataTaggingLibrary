package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class TodoNodeRef extends InstructionNodeRef {

	private final String todoText;
	
    public TodoNodeRef(String id, String body) {
        super( id );
		todoText = body;
    }

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visit(this);
	}

	public String getTodoText() {
		return todoText;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + Objects.hashCode(this.todoText);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof TodoNodeRef) ) {
			return false;
		}
		final TodoNodeRef other = (TodoNodeRef) obj;
		return Objects.equals(this.todoText, other.todoText)
				&& equalsAsInstructionNodeRef(other);
	}
	
	

}
