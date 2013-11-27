package edu.harvard.iq.datatags.parser.references;

import java.util.Collections;

/**
 *
 * @author michael
 */
public class ToDoTypeReference extends TypeReference {
	
	private final String comment;
	
	public ToDoTypeReference( String typeName, String aComment ) {
		super( typeName, Collections.emptyList() );
		comment = aComment;
	}
	
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitToDoTypeReference(this);
	}

	public String getComment() {
		return comment;
	}
	
}
