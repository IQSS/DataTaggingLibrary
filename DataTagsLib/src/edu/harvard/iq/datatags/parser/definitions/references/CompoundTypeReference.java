package edu.harvard.iq.datatags.parser.definitions.references;

import java.util.List;

/**
 * A parsed reference of a compound type.
 * @author michael
 */
public class CompoundTypeReference extends TypeReference {

	public CompoundTypeReference(String typeName, List<?> fieldTypeNames) {
		super( typeName, fieldTypeNames ); 
	}

	@Override
	public <T> T accept(Visitor<T> v) {
		return v.visitCompoundTypeReference(this);
	}
}
