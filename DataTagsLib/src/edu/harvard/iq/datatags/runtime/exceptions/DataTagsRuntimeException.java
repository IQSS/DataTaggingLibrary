package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Base class for exceptions that happen during the execution of
 * a {@link FlowChartSet}.
 * @author michael
 */
public class DataTagsRuntimeException extends RuntimeException {
	private final RuntimeEngine engine;

	public DataTagsRuntimeException(RuntimeEngine engine, String message) {
		super(message);
		this.engine = engine;
	}

	public DataTagsRuntimeException(RuntimeEngine engine, String message, Throwable cause) {
		super(message, cause);
		this.engine = engine;
	}

	public DataTagsRuntimeException(RuntimeEngine engine, Throwable cause) {
		super(cause);
		this.engine = engine;
	}

	public DataTagsRuntimeException(RuntimeEngine engine, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.engine = engine;
	}

	public RuntimeEngine getEngine() {
		return engine;
	}
	
}
