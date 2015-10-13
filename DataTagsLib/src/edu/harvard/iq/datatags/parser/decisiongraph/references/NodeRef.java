package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * Top level class of node references. Nothing much here.
 * @author Michael Bar-Sinai
 */
public class NodeRef {
    
    private String id;

	public NodeRef() {
	}

	public NodeRef(String id) {
		this.id = id;
	}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
	
	protected boolean equalsAsNodeRef( NodeRef other ) {
		return Objects.equals(id, other.getId());
	}
    
}
