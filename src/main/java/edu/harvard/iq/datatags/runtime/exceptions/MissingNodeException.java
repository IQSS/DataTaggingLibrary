package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Thrown when a node referred to is not there.
 * 
 * @author michael
 */
public class MissingNodeException extends LinkageException {
	
	private String nodeId;

	public MissingNodeException(RuntimeEngine engine, CallNode caller) {
		super( caller, engine, String.format("Node id '%s' does not exist. Called from node '%s'", caller.getCalleeNode(), caller.getId()) );
		nodeId = caller.getCalleeNode().getId();
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
