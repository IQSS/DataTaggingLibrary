package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class TextNodeRef extends NodeRef {
    
	private final String bodyText;
	
    public TextNodeRef( String id, String text ) {
        super( id );
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
		if ( ! (obj instanceof TextNodeRef) ) {
			return false;
		}
		final TextNodeRef other = (TextNodeRef) obj;
		
		return Objects.equals(this.bodyText, other.bodyText);
	}
	
	
}
