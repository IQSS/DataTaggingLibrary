package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 * A node that calls another node, possibly in another chart as well.
 * @author michael
 */
public class CallNode extends Node {
	
	private Node nextNode;
	private String calleeChartId;
	private String calleeId;
	
	public CallNode(String id) {
		super(id);
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visitCallNode(this);
	}
	
	public Node getNextNode() {
		return nextNode;
	}

	public void setNextNode(Node nextNode) {
		this.nextNode = nextNode;
	}

	public String getCalleeChartId() {
		return calleeChartId;
	}

	public void setCalleeChartId(String calleeChartId) {
		this.calleeChartId = calleeChartId;
	}	

	public String getCalleeNodeId() {
		return calleeId;
	}

	public void setCalleeId(String calleeId) {
		this.calleeId = calleeId;
	}
	
}
