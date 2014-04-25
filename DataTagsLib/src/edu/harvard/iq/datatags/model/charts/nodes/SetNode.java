package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that sets values in the data tags. Setting is done by means of composing 
 * the node's compound value with the engine's current tag value.
 * @author michael
 */
public class SetNode extends ThroughNode {
	
	/**
	 * The tags to be composed with the existing tags.
	 */
	private CompoundValue tags;

	public SetNode(CompoundValue tags, String id) {
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

	public void setTags(CompoundValue tags) {
		this.tags = tags;
	}	
}
