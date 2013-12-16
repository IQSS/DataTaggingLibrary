package edu.harvard.iq.datatags.parser.definitions.references;

import java.util.Objects;

/**
 * A reference to a name (of type, value, etc.). May have a comment.
 * @author michael
 */
public class NamedReference {
	
	private String name;
	private String comment;

	public NamedReference() {
	}

	public NamedReference(String name) {
		this.name = name;
	}

	public NamedReference(String name, String comment) {
		this.name = name;
		this.comment = comment;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 19 * hash + Objects.hashCode(this.name);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NamedReference other = (NamedReference) obj;
		if (!Objects.equals(this.name, other.name)) {
			return false;
		}
		return Objects.equals(this.comment, other.comment);
	}
	
	@Override
	public String toString() {
		return "[NamedReference name:" + getName()
				+ ( getComment()!=null ? (" comment:" + getComment()) : "")
				+ "]";
	}
	
}
