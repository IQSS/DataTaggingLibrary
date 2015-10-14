package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * A sub-node holding a question text.
 * {@code 
 *  [ask: {text: this is the text sub-node} ...]
 * }
 * @author Michael Bar-Sinai
 */
public class AstTextSubNode {
    
	private final String bodyText;
	
    public AstTextSubNode( String text ) {
		bodyText = text;
    }
    
    public String getText() { 
		return bodyText;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 41 * hash + Objects.hashCode(this.bodyText);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof AstTextSubNode) ) {
			return false;
		}
		final AstTextSubNode other = (AstTextSubNode) obj;
		
		return Objects.equals(this.bodyText, other.bodyText);
	}
	
	
}
