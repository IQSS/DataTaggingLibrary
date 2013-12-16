package edu.harvard.iq.datatags.parser.definitions.references;

import java.util.List;

/**
 * A result of parsing an expression describing a simple type.
 * @author michael
 */
public class SimpleTypeReference extends TypeReference {

	public SimpleTypeReference( String typeName, List<?> valueNames ) {
		super(typeName, valueNames);
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitSimpleTypeReference(this);
	}
	
}
