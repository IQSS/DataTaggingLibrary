package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.io.StringMapFormat;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.inference.AbstractValueInferrer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.*;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * The engine that executes a {@link DecisionGraph}.
 *
 * Intended usage pattern:  <code>
 *	if ( engine.start(node) ) {
 *		while ( engine.consume( getAnswerFromSomewhere() ) ) {}
 *  }
 * </code>
 *
 * @author michael
 */

 public class RuntimeEngine {

    public interface Listener {

        void runStarted(RuntimeEngine ngn);

        void processedNode(RuntimeEngine ngn, Node node);

        void runTerminated(RuntimeEngine ngn);

        void statusChanged(RuntimeEngine ngn);
        
        void sectionStarted(RuntimeEngine ngn, Node node);
        
        void sectionEnded(RuntimeEngine ngn, Node node);
    }

    /**
     * Used to give instances meaningful names.
     */
    private static final AtomicInteger COUNTER = new AtomicInteger(0);

    private String id = "RuntimeEngine-" + COUNTER.incrementAndGet();
    private PolicyModel model;
    private DecisionGraph decisionGraph;
    private CompoundValue currentValue;
    private final Deque<ThroughNode> stack = new LinkedList<>();
    private Node currentNode;
    private RuntimeEngineStatus status = RuntimeEngineStatus.Idle;
    private Optional<Listener> listener = Optional.empty();
    private Set<AbstractValueInferrer> valueInferrers;

    private final Node.Visitor<Node> processNodeVisitor = new Node.Visitor<Node>() {

        @Override
        public Node visit(ConsiderNode nd) {
               // check any answer to be part of the current DataTags 
               // return the node of the first write answer 
            for (ConsiderAnswer ans : nd.getAnswers()) {
                CompoundValue answer = ans.getAnswer();
                if (currentValue.isSupersetOf(answer)) {
                    return nd.getNodeFor(ans);
                }
            }
            
            return nd.getElseNode();
        }

        @Override

        public Node visit(AskNode nd) {
            // stop and consult the user.
            return null;
        }

        @Override
        public Node visit(ToDoNode nd) {
            // Skip!
            // TODO allow engines to stop here, it's a valid use-case.
            return nd.getNextNode();
        }

        @Override
            public Node visit(SetNode nd) {
            // Apply changes
            setCurrentValue(getCurrentValue().composeWith(nd.getTags()));
            
            // Process inferred values (ValueInference)
            CompoundValue previousValue;
            CompoundValue inferredValue = getCurrentValue();
            
            do {
                previousValue = inferredValue;
                CompoundValue infCapture = inferredValue; // passing to lambda, has to be effectively final.
                inferredValue = valueInferrers.stream().map( vi -> vi.apply(infCapture) ).collect( C.compose(previousValue.getSlot()));                
            } while ( !inferredValue.equals(previousValue) );
            
            if ( ! inferredValue.equals(getCurrentValue()) ) {
                setCurrentValue(inferredValue);
            }
            
            // Off we go to the next node.
            return nd.getNextNode();
        }

        @Override
        public Node visit(RejectNode nd) throws DataTagsRuntimeException {
            setStatus(RuntimeEngineStatus.Reject);
            return null;
        }

        @Override
        public Node visit(CallNode nd) throws DataTagsRuntimeException {
            stack.push(nd);
            // Dynamic linking to the destination node.
            Node calleeNode = nd.getCalleeNode();
            if (calleeNode == null) {
                setStatus(RuntimeEngineStatus.Error);
                throw new MissingNodeException(RuntimeEngine.this, nd);
            }

            // enter the linked node
            return calleeNode;
        }

        @Override
        public Node visit(EndNode nd) throws DataTagsRuntimeException {
            if (stack.isEmpty()) {
                setStatus(RuntimeEngineStatus.Accept);
                return null;
            } else {
                ThroughNode node = stack.pop();
                if (node instanceof SectionNode){
                    listener.ifPresent(l -> l.sectionEnded(RuntimeEngine.this, node));
                }
                return node.getNextNode();
            }
        }
        
        @Override
        public Node visit(SectionNode nd) throws DataTagsRuntimeException{
            listener.ifPresent(l -> l.sectionStarted(RuntimeEngine.this, nd));
            stack.push(nd);
            return nd.getStartNode();
        }
        
    };
    
    public CompoundValue getCurrentValue() {
        return currentValue;
    }

    /**
     * Starts a run, in the start node of the flowchart whose name was passed.
     * If there are no data tags for the engine, a new instance is created.
     * Otherwise, the current data tags are retained.
     *
     * @return {@code true} iff there is a need to consume answers.
     */
    public boolean start() throws DataTagsRuntimeException {
        setCurrentValue(model.getSpaceRoot().createInstance());
        setStatus(RuntimeEngineStatus.Running);
        listener.ifPresent(l -> l.runStarted(this));
        
        return processNode(decisionGraph.getStart());
    }

    /**
     * Terminates current run, clears the state and goes back to node 1.
     */
    public void restart() {
        if ( model == null ) return;
        listener.ifPresent(l -> l.runTerminated(this));
        setStatus(RuntimeEngineStatus.Restarting);
        stack.clear();
        currentNode = null;
        
        start();
    }

    /**
     * Sets the status to idle, removes all state related to runtime.
     */
    public void setIdle() {
        setStatus(RuntimeEngineStatus.Idle);
        stack.clear();
        currentNode = null;
        currentValue = null;
    }

    protected boolean processNode(Node n) throws DataTagsRuntimeException {
        Node next = n;
        do {
            currentNode = next; // advance program counter
            next = currentNode.accept(processNodeVisitor);
            listener.ifPresent(l -> l.processedNode(this, getCurrentNode()));
        } while (next != null);

        return getStatus() == RuntimeEngineStatus.Running;
    }

    /**
     * Advances the engine to the node appropriate for the passed answer.
     *
     * @param ans the answer we got from the user
     * @return {@code true} iff there is where to advance to.
     * @throws DataTagsRuntimeException
     */
    public boolean consume(Answer ans) throws DataTagsRuntimeException {
        AskNode current = (AskNode) currentNode;
        Node next = current.getNodeFor(ans);
        return processNode(next);
    }

    public RuntimeEngineState createSnapshot() {
        final RuntimeEngineState state = new RuntimeEngineState();

        state.setStatus(getStatus());
        state.setCurrentNodeId(getCurrentNode().getId());

        getStack().forEach(nd -> state.pushNodeIdToStack(nd.getId()));

        state.setSerializedTagValue(new StringMapFormat().format(currentValue));

        return state;
    }

    public void applySnapshot(RuntimeEngineState snapshot) {
        if (snapshot == null) {
            throw new IllegalArgumentException("Snapshot cannot be null");
        }
        setStatus(snapshot.getStatus());
        currentValue = new StringMapFormat().parseCompoundValue(
                model.getSpaceRoot(),
                snapshot.getSerializedTagValue());
        currentNode = decisionGraph.getNode(snapshot.getCurrentNodeId());

        stack.clear();
        snapshot.getStack().forEach((nodeId) -> stack.push((ThroughNode) decisionGraph.getNode(nodeId)));

    }

    /**
     * Convenience method for consuming multiple answers.
     *
     * @param answers
     * @return {@code true} iff there is where to advance to.
     * @throws DataTagsRuntimeException
     */
    public boolean consumeAll(Iterable<Answer> answers) throws DataTagsRuntimeException {
        Iterator<Answer> it = answers.iterator();
        boolean res = true;
        while (it.hasNext()) {
            res = consume(it.next());
            if (!res) {
                return false;
            }
        }
        return res;
    }

    public boolean consumeAll(Answer... answers) throws DataTagsRuntimeException {
        return consumeAll(Arrays.asList(answers));
    }

    public void setCurrentValue(CompoundValue aNewValue) {
        this.currentValue = aNewValue;
    }

    /**
     * @return The current stack of nodes. This is enough to know where the
     * engine is, but not what the data tags state is.
     */
    public Deque<ThroughNode> getStack() {
        return stack;
    }

    public String getRejectionReason() {
        return (currentNode instanceof RejectNode) ? ((RejectNode) currentNode).getReason() : null;
    }

    /**
     * @return The node the engine is currently in.
     */
    public Node getCurrentNode() {
        return currentNode;
    }

    public Listener getListener() {
        return listener.orElse(null);
    }

    /**
     * Sets the listener on the engine.
     *
     * @param <T> The type of the listener
     * @param listener well, duh
     * @return The listener, to allow setting and assignment in the same
     * expression
     */
    public <T extends Listener> T setListener(T listener) {
        this.listener = Optional.ofNullable(listener);
        return listener;
    }

    /**
     * The id of the engine is used for logging purposes, same as the name field
     * in the Thread class.
     *
     * @return The engine ID.
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public RuntimeEngineStatus getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    protected void setStatus(RuntimeEngineStatus status) {
        this.status = status;
        listener.ifPresent(l -> l.statusChanged(this));
    }

    public PolicyModel getModel() {
        return model;
    }

    public void setModel(PolicyModel model) {
        this.model = model;
        decisionGraph = model.getDecisionGraph();
        valueInferrers = model.getValueInferrers();
        listener.ifPresent( l->l.runTerminated(this) );
        setStatus(RuntimeEngineStatus.Idle);
        
    }
    
}
