package edu.harvard.iq.datatags.parser.references;

import java.util.List;

/**
 * The result of parsing a short aggregate type expression.
 *
 * @author michael
 */
public class AggregateTypeReference extends TypeReference {

	public AggregateTypeReference(String typeName, List<String> someSubValueNames) {
		super(typeName, someSubValueNames);
	}
	
}
