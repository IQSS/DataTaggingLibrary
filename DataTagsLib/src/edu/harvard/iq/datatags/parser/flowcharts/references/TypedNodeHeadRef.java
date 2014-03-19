package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * A reference to a head of node in the parsed AST. Node heads
 * contain node type, and may contain ID.
 * TODO this class should probably go.
 * 
 * @author michael
 */
public class TypedNodeHeadRef extends AbstractNodeHeadRef {
	private final NodeType type;

	public TypedNodeHeadRef(String id, NodeType type) {
		super( id );
		this.type = type;
	}

	public NodeType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.type);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof TypedNodeHeadRef) ) {
			return false;
		}
		final TypedNodeHeadRef other = (TypedNodeHeadRef) obj;
		if (!Objects.equals(getId(), other.getId())) {
			return false;
		}
		return this.type == other.type;
	}
	
	@Override
    public String toString() {
        return "[headref id:" + getId() + " type:" + getType() + "]";
    }
	
}
