package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that sets values in the data tags. Setting is done by composing 
 * the node's compound value with the engine's current tag value.
 * @author michael
 */
public class SetNode extends ThroughNode {
	
	/**
	 * The tags to be composed with the existing tags.
	 */
	private final CompoundValue tags;

	public SetNode(String id, CompoundValue tags) {
		super(id);
		this.tags = tags;
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit( this );
	}
	
	public CompoundValue getTags() {
		return tags;
	}

    @Override
    public String toStringExtras() {
        return "tags:" + tags.toString();
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof SetNode) ) {
            return false;
        }
        final SetNode other = (SetNode) obj;
        
        return Objects.equals(this.tags, other.tags) && equalsAsNode(other);
    }
    
}
