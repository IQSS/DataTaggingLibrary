package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.runtime.CallNode;
import edu.harvard.iq.datatags.runtime.FlowChartSet;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Base class for exceptions thrown when the runtime engine
 * can't find some entity.
 * 
 * @author michael
 */
public class LinkageException extends DataTagsRuntimeException {
	
	private FlowChartSet chartSet;
	private CallNode sourceNode;

	public LinkageException(FlowChartSet chartSet, RuntimeEngine engine, String message) {
		super(engine, message);
		this.chartSet = chartSet;
	}

	public LinkageException(FlowChartSet chartSet, RuntimeEngine engine, String message, Throwable cause) {
		super(engine, message, cause);
		this.chartSet = chartSet;
	}

	public LinkageException(FlowChartSet chartSet, RuntimeEngine engine, Throwable cause) {
		super(engine, cause);
		this.chartSet = chartSet;
	}

	public LinkageException(FlowChartSet chartSet, RuntimeEngine engine, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(engine, message, cause, enableSuppression, writableStackTrace);
		this.chartSet = chartSet;
	}
	
	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}

	public CallNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(CallNode sourceNode) {
		this.sourceNode = sourceNode;
	}
	
}
