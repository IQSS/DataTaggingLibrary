package edu.harvard.iq.datatags.model.types;

import java.util.Objects;

/**
 * A type of a value a data tag may have.
 * 
 * @author michael
 */
public abstract class TagType {
	
	private final String name;
	private final String info;

	public TagType(String name, String info) {
		this.name = name;
		this.info = info;
	}

	public String getInfo() {
		return info;
	}

	public String getName() {
		return name;
	}

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
