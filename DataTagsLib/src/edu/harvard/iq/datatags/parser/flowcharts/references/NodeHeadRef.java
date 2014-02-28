package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * A reference to a head of node in the parsed AST. Node heads
 * contain node type, and may contain ID.
 * 
 * @author michael
 */
public class NodeHeadRef {
	private final String id;
	private final NodeType type;

	public NodeHeadRef(String id, NodeType type) {
		this.id = id;
		this.type = type;
	}

	public String getId() {
		return id;
	}

	public NodeType getType() {
		return type;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + Objects.hashCode(this.id);
		hash = 29 * hash + Objects.hashCode(this.type);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof NodeHeadRef) ) {
			return false;
		}
		final NodeHeadRef other = (NodeHeadRef) obj;
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		return this.type == other.type;
	}
	
	
	
}
