package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.Objects;

/**
 * A type of a value a data tag may have.
 * 
 * @author michael
 */
public abstract class TagType {
	
	public interface Visitor<T> {
		T visitSimpleType( SimpleType t );
		T visitAggregateType( AggregateType t );
		T visitCompoundType( CompoundType t );
		T visitTodoType( ToDoType t );
	}
	
	private final String name;
	private String info;

	public TagType(String name, String info) {
		this.name = name;
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}
	
	public String getName() {
		return name;
	}

	/**
	 * Generate a new instance of the type. Preferable over using direct construction.
	 * @param name name of the new instance
	 * @param info info about the new instance
	 * @return the new instance.
	 */
	public abstract TagValue make( String name, String info );
	
	public abstract <T> T accept( Visitor<T> v );
	
	@Override
	public int hashCode() {
		int hash = 3;
		hash = 89 * hash + Objects.hashCode(this.name);
		hash = 89 * hash + Objects.hashCode(this.info);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof TagType) ) {
			return false;
		}
		final TagType other = (TagType) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
	
		return Objects.equals(this.info, other.info);
	}

	@Override
	public String toString() {
		String[] className = getClass().getName().split("\\.");
		return String.format("[%s name:%s]", className[className.length-1], getName());
	}
	
	
}
