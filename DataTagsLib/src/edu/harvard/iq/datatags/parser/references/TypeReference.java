package edu.harvard.iq.datatags.parser.references;

import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.List;
import java.util.Objects;

/**
 * An result of parsing an expression that defines a type.
 * @author michael
 */
public class TypeReference {
	private final String typeName;
	private final List<String> subValueNames;
	
	public TypeReference(String typeName, List<String> someSubValueNames) {
		this.typeName = typeName;
		subValueNames = someSubValueNames;
	}
	
	public String getTypeName() {
		return typeName;
	}

	@Override
	public int hashCode() {
		return typeName.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( obj.getClass().isAssignableFrom(obj.getClass()) ) {
			final TypeReference other = (TypeReference) obj;
			return Objects.equals(this.typeName, other.typeName);
		}
		return false;
	}

	@Override
	public String toString() {
		String className = C.last(getClass().getName().split("\\."));
		return String.format("[%s typeName:%s subs:%s %s]", className, getTypeName(), getSubValueNames(), toStringExtras());
	}
	
	protected String toStringExtras() {
		return "";
	}

	public List<String> getSubValueNames() {
		return subValueNames;
	}
	
}
