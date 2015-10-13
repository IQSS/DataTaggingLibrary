package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * A part of the body of a node. Takes the form of {@code (title: value)} in the
 * syntax.
 * @param <ValueType>  the type of the value - normally a String, but other, more 
 *                     complex types are also feasible.
 * @author michael
 */
public class NodeBodyPart<ValueType> {
	private final String title;
	private final ValueType value;

	public NodeBodyPart(String title, ValueType value) {
		this.title = title;
		this.value = value;
	}

	public String getTitle() {
		return title;
	}

	public ValueType getValue() {
		return value;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 53 * hash + Objects.hashCode(this.title);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof NodeBodyPart) ) {
			return false;
		}
		final NodeBodyPart<?> other = (NodeBodyPart<?>) obj;
		if (!Objects.equals(this.title, other.title)) {
			return false;
		}
		return Objects.equals(this.value, other.value);
	}
}
