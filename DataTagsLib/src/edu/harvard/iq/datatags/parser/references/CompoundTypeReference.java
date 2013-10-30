package edu.harvard.iq.datatags.parser.references;

import java.util.List;
import java.util.Objects;

/**
 * A parsed reference of a compound type.
 * @author michael
 */
public class CompoundTypeReference extends TypeReference {

	public CompoundTypeReference(String typeName, List<String> fieldTypeNames) {
		super( typeName, fieldTypeNames ); 
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + super.hashCode();
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( obj instanceof CompoundTypeReference ) {
			final CompoundTypeReference other = (CompoundTypeReference) obj;
			return super.equals(obj);
		}
		return false;
	}
}
