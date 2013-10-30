package edu.harvard.iq.datatags.parser.references;

import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * An result of parsing an expression that defines a type.
 * @author michael
 */
public abstract class TypeReference {
	
	public interface Visitor<T> {
		T visitSimpleTypeReference( SimpleTypeReference ref );
		T visitAggregateTypeReference( AggregateTypeReference ref );
		T visitCompoundTypeReference( CompoundTypeReference ref );
	}
	
	private final String typeName;
	private final List<NamedReference> subValueNames;
	
	public TypeReference(String typeName, List<?> someSubValueNames) {
		this.typeName = typeName;
		subValueNames = new ArrayList<>(someSubValueNames.size());
		for ( Object o : someSubValueNames ) {
			subValueNames.add( (o instanceof NamedReference) ?
									(NamedReference)o
									:new NamedReference(o.toString()) );
		}
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
			return Objects.equals(this.typeName, other.typeName)
					&& this.subValueNames.equals(other.getSubValueNames());
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

	public List<NamedReference> getSubValueNames() {
		return subValueNames;
	}
	
	public abstract <T> T accept( Visitor<T> v );
}
