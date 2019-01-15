package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AstTodoNode extends AstNode {

	private final String todoText;
	
    public AstTodoNode(String id, String body) {
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
		if ( ! (obj instanceof AstTodoNode) ) {
			return false;
		}
		final AstTodoNode other = (AstTodoNode) obj;
		return Objects.equals(this.todoText, other.todoText)
				&& equalsAsAstNode(other);
	}
	
	@Override
    protected String toStringExtras() {
        return "text:<" + getTodoText() + ">";
    }

}
