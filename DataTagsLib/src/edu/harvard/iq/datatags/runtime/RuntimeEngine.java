package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.exceptions.MissingFlowChartException;
import edu.harvard.iq.datatags.runtime.exceptions.MissingNodeException;
import edu.harvard.iq.datatags.model.DataTags;
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
	
	private final Node.Visitor<Boolean> enterNodeVisitor = new Node.Visitor<Boolean>() {
		@Override
		public Boolean visitDecisionNode(DecisionNode nd) {
			setCurrentTags( getCurrentTags().composeWith(nd.getTags()) );
			stack.push(nd);
			if ( listener != null ) listener.nodeEntered(RuntimeEngine.this, nd);
			
			return true;
		}

		@Override
		public Boolean visitCallNode(CallNode nd) throws DataTagsRuntimeException {
			stack.push(nd);
			
			// try to make the link
			FlowChart fs = getChartSet().getFlowChart(nd.getCalleeChartId());
			if ( fs == null ) {
				MissingFlowChartException mfce = new MissingFlowChartException(nd.getCalleeChartId(), chartSet, RuntimeEngine.this, "Can't find chart " + nd.getCalleeChartId() );
				mfce.setSourceNode(nd);
				throw mfce;
			}
			Node nextNode = fs.getNode(nd.getCalleeNodeId());
			if ( nextNode == null ) {
				throw new MissingNodeException(chartSet, RuntimeEngine.this, nd);
			}
			
			// enter the linked node
			return enterNode( nextNode );
		}

		@Override
		public Boolean visitEndNode(EndNode nd) throws DataTagsRuntimeException {
			if ( stack.isEmpty() ) {
				// done running
				if ( listener != null ) listener.runTerminated(RuntimeEngine.this);
				return false;
			} else {
				// stack top has to be a call node.
				CallNode cn = (CallNode) stack.peek();
				return enterNode( cn.getNextNode() );
			}
		}
	};
	
	
	public DataTags getCurrentTags() {
		return currentTags;
	}
	
	/**
	 * Starts a run, in the start node of the flowchart whose name was passed.
	 * If there are no data tags for the engine, a new instance is created. Otherwise,
	 * the current data tags are retained.
	 * 
	 * @param flowChartName name of the chart to start running at.
	 * @throws MissingFlowChartException if that chart does not exist.
	 */
	public void start( String flowChartName ) throws DataTagsRuntimeException {
		FlowChart fs = chartSet.getFlowChart(flowChartName);
		if ( fs == null ) {
			throw new MissingFlowChartException(flowChartName, 
					chartSet, this, 
					String.format("FlowChart named '%s' cannot be found",flowChartName));
		}
		
		if ( getCurrentTags() == null ) {
			setCurrentTags( new DataTags() );
		}
		if ( listener!=null ) listener.runStarted(this);
		stack.push( new CallNode("DUMMY") );
		enterNode( fs.getStart() );
		
	}
	
	boolean enterNode( Node n ) throws DataTagsRuntimeException {
		stack.pop(); // remove last chart node.
		return n.accept( enterNodeVisitor );
	}
	
	public boolean consume( Answer ans ) throws DataTagsRuntimeException {
		DecisionNode current = (DecisionNode) stack.peek();
		Node next = current.getNodeFor(ans);
		return enterNode( next );
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
