package edu.harvard.iq.datatags.runtime;

/**
 * A node that calls another node, possibly in another chart as well.
 * @author michael
 */
public class CallNode extends Node {
	
	private Node nextNode;
	private String calleeChartId;
	private String calleId;
	
	public CallNode(String id) {
		super(id);
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

	public String getCalleId() {
		return calleId;
	}

	public void setCalleId(String calleId) {
		this.calleId = calleId;
	}
	
	
	
}
