package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.model.DataTags;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that sets values in the data tags.
 * @author michael
 */
public class SetNode extends ThroughNode {
	
	/**
	 * The tags to be composed with the existing tags.
	 */
	private DataTags tags;

	public SetNode(DataTags tags, String id) {
		super(id);
		this.tags = tags;
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit( this );
	}
	
	public DataTags getTags() {
		return tags;
	}

	public void setTags(DataTags tags) {
		this.tags = tags;
	}	
}
