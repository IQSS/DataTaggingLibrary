package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tags.DataTags;
import java.util.Deque;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 * The engine that executes {@link FlowChartSet}s.
 * 
 * @author michael
 */
public class RuntimeEngine {
	
	public interface Listener {
		public void runStarted( RuntimeEngine ngn );
		public void nodeEntered( RuntimeEngine ngn, Node node );
		public void runTerminated( RuntimeEngine ngn );
		public void runError( RuntimeEngine ngn, DataTagsRuntimeException e );
	}
	
	private static final AtomicInteger COUNTER = new AtomicInteger(0);
	
	private String id = "RuntimeEngine-" + COUNTER.incrementAndGet();
	private FlowChartSet chartSet;
	private DataTags currentTags;
	private final Deque<Node> stack = new LinkedList<>();
	private Listener listener;
	
	public DataTags getCurrentTags() {
		return currentTags;
	}

	public void setCurrentTags(DataTags currentTags) {
		this.currentTags = currentTags;
	}

	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}
	
	/**
	 * @return The current stack of nodes. This is enough to know where 
	 * the engine is, but not what the data tags state is.
	 */
	public Deque<Node> getStack() {
		return stack;
	}

	public Listener getListener() {
		return listener;
	}

	public void setListener(Listener listener) {
		this.listener = listener;
	}
	
	/**
	 * The id of the engine is used for logging purposes, same as the name 
	 * field in the Thread class.
	 * @return The engine ID.
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	
}
