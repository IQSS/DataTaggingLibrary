package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * A reference to a single parsed node. Created by the AST parser.
 * @author michael
 */
public class NodeReference {
	private final NodeHeadReference head;
	private final Map<String, NodeBodyPart<?>> body = new TreeMap<>();

	public NodeReference(NodeHeadReference head) {
		this.head = head;
	}
	
	public void add( NodeBodyPart<?> part ) {
		body.put(part.getTitle(), part);
	}
	
	public NodeBodyPart<?> get( String bodyPartName ) {
		return body.get(bodyPartName);
	}
	
	public <T> NodeBodyPart<T> getAs( String bodyPartName, Class<T> t ) {
		if ( body.containsKey(bodyPartName) ) {
			NodeBodyPart<?> nbp = get( bodyPartName );
			if ( t.isAssignableFrom(nbp.getValue().getClass()) ) {
				return (NodeBodyPart<T>)nbp;
			}
			throw new ClassCastException("Cannot cast " + nbp.getValue().getClass() + " to " + t );
		}
		return null;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.head);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof NodeReference) ) {
			return false;
		}
		final NodeReference other = (NodeReference) obj;
		if (!Objects.equals(this.head, other.head)) {
			return false;
		}
		if (!Objects.equals(this.body, other.body)) {
			return false;
		}
		return true;
	}
	
	
}
