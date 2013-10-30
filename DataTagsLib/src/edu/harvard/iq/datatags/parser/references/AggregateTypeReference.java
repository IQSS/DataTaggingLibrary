package edu.harvard.iq.datatags.parser.references;

import java.util.List;

/**
 * The result of parsing a short aggregate type expression.
 *
 * @author michael
 */
public class AggregateTypeReference extends TypeReference {

	public AggregateTypeReference(String typeName, List<?> someSubValueNames) {
		super(typeName, someSubValueNames);
	}
	
	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitAggregateTypeReference(this);
	}
}
