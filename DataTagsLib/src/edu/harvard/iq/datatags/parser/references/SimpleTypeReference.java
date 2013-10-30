package edu.harvard.iq.datatags.parser.references;

import java.util.List;

/**
 * A result of parsing an expression describing a simple type.
 * @author michael
 */
public class SimpleTypeReference extends TypeReference {

	public SimpleTypeReference( String typeName, List<String> valueNames ) {
		super(typeName, valueNames);
	}
	
}
