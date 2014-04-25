package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.io.StringMapFormat;
import edu.harvard.iq.datatags.model.charts.*;
import edu.harvard.iq.datatags.model.charts.nodes.*;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.*;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Intended usage pattern:
 * <code>
 *	if ( engine.start(node) ) {
 *		while ( engine.consume( getAns() ) ) {}
 *  }
 * </code>
 * The engine that executes {@link FlowChartSet}s.
 * 
 * @author michael
 */
public class RuntimeEngine {
	
	public enum Status { 
        /** The engine has not run yet */
        Idle, 
        /** Engine is currently processing. */
        Running,
        /** The engine has concluded we cannot accept this dataset (sorry!).*/
        Reject, 
        /** The engine is happy to accept the dataset, as long as the data tags requirements are complied with. */
        Accept, 
        /** An error has occurred. Please ignore the status of the engine. */
        Error }
	
	public interface Listener {
		public void runStarted( RuntimeEngine ngn );
		public void processedNode( RuntimeEngine ngn, Node node );
		public void runTerminated( RuntimeEngine ngn );
		// TODO find usages, consider removing, as we throw exceptions anyway.
		public void runError( RuntimeEngine ngn, DataTagsRuntimeException e );
	}
	
	/** Used to give instances meaningful names. */
	private static final AtomicInteger COUNTER = new AtomicInteger(0);
		
	private String id = "RuntimeEngine-" + COUNTER.incrementAndGet();
	private FlowChartSet chartSet;
    
    // LATER for completeness, this should be TagValue, not just CompoundTagValue
	private CompoundValue currentTags;
	private final Deque<CallNode> stack = new LinkedList<>();
	private Node currentNode;
	private Status status = Status.Idle;
	private Listener listener;
	
	private final Node.Visitor<Node> processNodeVisitor = new Node.Visitor<Node>() {
		
		@Override
		public Node visit( AskNode nd ) {
			// stop and consult the user.
			return null;
		}
		
		@Override
		public Node visit( TodoNode nd ) {
			// Skip!
            // TODO allow engines to stop here, it's a valid use-case.
			return nd.getNextNode();
		}
		
		@Override
		public Node visit( SetNode nd ) {
			// Apply changes
			setCurrentTags( getCurrentTags().composeWith(nd.getTags()) );
			
			// Off we go to the next node.
			return nd.getNextNode();
		}

        @Override
        public Node visit(RejectNode nd) throws DataTagsRuntimeException {
            status = Status.Reject;
            return null;
        }

		@Override
		public Node visit( CallNode nd ) throws DataTagsRuntimeException {
			stack.push(nd);
			// Dynamic linking to the destination node.
			FlowChart fs = getChartSet().getFlowChart(nd.getCalleeChartId());
			if ( fs == null ) {
				MissingFlowChartException mfce = new MissingFlowChartException(nd.getCalleeChartId(), chartSet, RuntimeEngine.this, "Can't find chart " + nd.getCalleeChartId() );
				mfce.setSourceNode(nd);
				status = Status.Error;
				throw mfce;
			}
			Node calleeNode = fs.getNode(nd.getCalleeNodeId());
			if ( calleeNode == null ) {
				status = Status.Error;
				throw new MissingNodeException(chartSet, RuntimeEngine.this, nd);
			}
			
			// enter the linked node
			return calleeNode;
		}

		@Override
		public Node visit( EndNode nd ) throws DataTagsRuntimeException {
			if ( stack.isEmpty() ) {
				status = Status.Accept;
				return null;
			} else {
				return stack.pop().getNextNode();
			}
		}
	};
	
	public CompoundValue getCurrentTags() {
		return currentTags;
	}
	
	/**
	 * Starts a run, in the start node of the flowchart whose name was passed.
	 * If there are no data tags for the engine, a new instance is created. Otherwise,
	 * the current data tags are retained.
	 * 
	 * @param flowChartName name of the chart to start running at.
	 * @return {@code true} iff there is a need to consume answers.
	 * @throws MissingFlowChartException if that chart does not exist.
	 */
	public boolean start( String flowChartName ) throws DataTagsRuntimeException {
		FlowChart fs = chartSet.getFlowChart(flowChartName);
		if ( fs == null ) {
			throw new MissingFlowChartException(flowChartName, 
					chartSet, this, 
					String.format("FlowChart named '%s' cannot be found",flowChartName));
		}
		
		if ( getCurrentTags() == null ) {
			setCurrentTags( ((CompoundType)chartSet.getTopLevelType()).createInstance() );
		}
		status = Status.Running;
		if ( listener!=null ) listener.runStarted(this);
		return processNode( fs.getStart() );
	}
	
	protected boolean processNode( Node n ) throws DataTagsRuntimeException {
		Node next = n;
		do {
			currentNode = next; // advance program counter
			next = currentNode.accept(processNodeVisitor);
			if ( listener != null ) listener.processedNode(this, getCurrentNode());
		} while ( next != null );

		return getStatus() == Status.Running;
	}
	
	/**
	 * Advances the engine to the node appropriate for the
	 * passed answer.
	 * @param ans the answer we got from the user
	 * @return {@code true} iff there is where to advance to.
	 * @throws DataTagsRuntimeException 
	 */
	public boolean consume( Answer ans ) throws DataTagsRuntimeException {
		AskNode current = (AskNode) currentNode;
		Node next = current.getNodeFor(ans);
		return processNode( next );
	}
	
    public RuntimeEngineState createSnapshot() {
        final RuntimeEngineState state = new RuntimeEngineState();
        
        state.setStatus(getStatus());
        state.setCurrentChartId( getCurrentNode().getChart().getId() );
        state.setCurrentNodeId( getCurrentNode().getId() );
        state.setFlowchartSetSource( getChartSet().getSource() );
        state.setFlowchartSetVersion( getChartSet().getVersion() );
        
        for ( Node nd : getStack() ) {
            state.pushNodeIdToStack( nd.getChart() + "/" + nd.getId() );
        }
        
        state.setSerializedTagValue( new StringMapFormat().format(currentTags) );
        
        return state;
    }
    
    public void applySnapShot( RuntimeEngineState snapshot ) {
        
        status = snapshot.getStatus();
        
        currentTags = (CompoundValue) new StringMapFormat().parse(currentTags.getType(), snapshot.getSerializedTagValue());
        currentNode = chartSet.getFlowChart( snapshot.getCurrentChartId() ).getNode( snapshot.getCurrentNodeId() );
        
        stack.clear();
        for ( String nodeId : snapshot.getStack() ) {
            String[] comps = nodeId.split("/");
            stack.push( (CallNode) chartSet.getFlowChart(comps[0]).getNode(comps[1]) );
        }
        
    }
    
	/**
	 * Convenience method for consuming multiple answers.
	 * @param answers 
	 * @return {@code true} iff there is where to advance to.
	 * @throws DataTagsRuntimeException 
	 */
	public boolean consumeAll( Iterable<Answer> answers ) throws DataTagsRuntimeException {
		Iterator<Answer> it = answers.iterator();
		boolean res = true;
		while ( it.hasNext() ) {
			res = consume( it.next() );
			if ( ! res ) return false;
		}
		return res;
	}
	
	public boolean consumeAll( Answer... answers ) throws DataTagsRuntimeException {
		return consumeAll( Arrays.asList(answers) );
	}
	
	public void setCurrentTags( CompoundValue currentTags) {
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
	public Deque<CallNode> getStack() {
		return stack;
	}
	
    public String getRejectionReason() {
        return ( currentNode instanceof RejectNode ) ? ((RejectNode)currentNode).getReason() : null;
    }
    
	/**
	 * @return The node the engine is currently in.
	 */
	public Node getCurrentNode() { 
		return currentNode;
	}
	
	public Listener getListener() {
		return listener;
	}

	/**
	 * Sets the listener on the engine.
	 * @param <T> The type of the listener
	 * @param listener well, duh
	 * @return The listener, to allow setting and assignment in the same expression
	 */
	public <T extends Listener> T setListener( T listener) {
		this.listener = listener;
		return listener;
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

	public Status getStatus() {
		return status;
	}
	
}
