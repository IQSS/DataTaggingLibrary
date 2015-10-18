package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.model.graphs.FlowChartSet;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Thrown when the runtime was referred to a nonexistent chart.
 * @author michael
 */
public class MissingFlowChartException extends LinkageException {
	
	private String flowChartName;

	public MissingFlowChartException(String flowChartName, FlowChartSet chartSet, RuntimeEngine engine, String message) {
		super(chartSet, engine, message);
		this.flowChartName = flowChartName;
	}

	public MissingFlowChartException(String flowChartName, FlowChartSet chartSet, RuntimeEngine engine, String message, Throwable cause) {
		super(chartSet, engine, message, cause);
		this.flowChartName = flowChartName;
	}

	public MissingFlowChartException(String flowChartName, FlowChartSet chartSet, RuntimeEngine engine, Throwable cause) {
		super(chartSet, engine, cause);
		this.flowChartName = flowChartName;
	}

	public MissingFlowChartException(String flowChartName, FlowChartSet chartSet, RuntimeEngine engine, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(chartSet, engine, message, cause, enableSuppression, writableStackTrace);
		this.flowChartName = flowChartName;
	}

	public String getFlowChartName() {
		return flowChartName;
	}

	public void setFlowChartName(String flowChartName) {
		this.flowChartName = flowChartName;
	}
	
}
