package edu.harvard.iq.datatags.runtime.exceptions;

import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * Base class for exceptions thrown when the runtime engine
 * can't find some entity.
 * 
 * @author michael
 */
public class LinkageException extends DataTagsRuntimeException {
	
	private CallNode sourceNode;

	public LinkageException(CallNode aSourceNode, RuntimeEngine anEngine, String aMessage) {
		super(anEngine, aMessage);
        sourceNode = aSourceNode;
	}

	public LinkageException( RuntimeEngine engine, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(engine, message, cause, enableSuppression, writableStackTrace);
	}
	
	public CallNode getSourceNode() {
		return sourceNode;
	}

	public void setSourceNode(CallNode sourceNode) {
		this.sourceNode = sourceNode;
	}
	
}
