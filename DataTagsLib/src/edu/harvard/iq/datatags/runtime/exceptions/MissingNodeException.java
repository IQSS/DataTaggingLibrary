package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Thrown when a node referred to is not there.
 * 
 * @author michael
 */
public class MissingNodeException extends LinkageException {
	
	private String nodeId;

	public MissingNodeException(FlowChartSet chartSet, RuntimeEngine engine, CallNode caller) {
		super(chartSet, engine, String.format("Node id '%s' does not exist in chart '%s'", caller.getCalleeNodeId(), caller.getCalleeNodeId()) );
		setSourceNode(caller);
		nodeId = caller.getCalleeNodeId();
	}
	
	public MissingNodeException(FlowChartSet chartSet, RuntimeEngine engine, String nodeId, String message) {
		super(chartSet, engine, message);
		this.nodeId = nodeId;
	}
	
	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	
}
