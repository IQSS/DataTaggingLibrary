package edu.harvard.iq.datatags.tools.queries;

import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderOption;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.joining;

/**
 * Gets a decision graph and a {@link CompoundValue}, and returns all the runs
 * that result in this value, or in a value that is a superset of this value.
 * 
 * The "Dgq" in the class name stand for "Decision Graph Query". We'll have more of those.
 * 
 * @author michael
 */
public class FindSupertypeResultsDgq implements DecisionGraphQuery {
    private final PolicyModel subject;
    private final CompoundValue value;
    private GraphTraverser graphTraverser;
    private boolean debugMode = false;
    
    public FindSupertypeResultsDgq( PolicyModel aPolicyModel, CompoundValue aValue) {
        subject = aPolicyModel;
        value = aValue;
    }
    
    public void get( DecisionGraphQuery.Listener aListener ) {
        graphTraverser = new GraphTraverser(aListener);
        aListener.started(this);
        subject.getDecisionGraph().getStart().accept(graphTraverser);
        aListener.done(this);
    }

    @Override
    public RunTrace getCurrentTrace() {
        return new RunTrace(graphTraverser.currentTrace, graphTraverser.currentAnswers, graphTraverser.valueStack.peek());
    }
    
    class GraphTraverser extends Node.VoidVisitor {
        
        final DecisionGraphQuery.Listener listener;
        
        LinkedList<Node> currentTrace = new LinkedList<>();
        LinkedList<List<ThroughNode>> nodeStackStack = new LinkedList<>();
        LinkedList<Answer> currentAnswers = new LinkedList<>();
        Deque<CompoundValue> valueStack = new LinkedList<>();
        
        public GraphTraverser( DecisionGraphQuery.Listener aListener ) {
            listener = aListener;
            valueStack.push( subject.getSpaceRoot().createInstance() );
            nodeStackStack.push( Collections.emptyList() );
        }
        
        @Override
        public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast( nd );
            for ( Answer ans : nd.getAnswers() ) {
                currentAnswers.addLast(ans);
                // process answer nodes
                nd.getNodeFor(ans).accept(this);
                
                currentAnswers.removeLast();
            }
            currentTrace.removeLast();
        }
        
        @Override
        public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast( nd );
            boolean matchFound = false;
            for (ConsiderOption ans : nd.getAnswers()) {
                CompoundValue answer = ans.getValue();
                if (valueStack.peek().isSupersetOf(answer)) {
                    matchFound = true;
                    nd.getNodeFor(ans).accept(this);
                }
            }
            if ( ! matchFound ) {
                nd.getElseNode();
            }
            
            currentTrace.removeLast();
        }
        
        @Override
        public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast(nd);
            valueStack.push( valueStack.peek().composeWith(nd.getTags()) );
            
            //Value inference
            // Process inferred values (ValueInference)
            CompoundValue previousValue;
            CompoundValue inferredValue = valueStack.peek();
            
            if ( ! subject.getValueInferrers().isEmpty() ) {
                do {
                    previousValue = inferredValue;
                    CompoundValue infCapture = inferredValue; // passing to lambda, has to be effectively final.
                    inferredValue = subject.getValueInferrers().stream().map( vi -> vi.apply(infCapture) ).collect( C.compose(previousValue.getSlot()));                
                } while ( !inferredValue.equals(previousValue) );

                if ( ! inferredValue.equals(valueStack.peek()) ) {
                    valueStack.pop();
                    valueStack.push(inferredValue);
                }
            }
            
            // go forward
            nd.getNextNode().accept(this);
            
            // came back, clean up.
            valueStack.pop();
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            // This runs is not a match.
            listener.rejectionFound(FindSupertypeResultsDgq.this);
        }

        @Override
        public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast(nd);
            pushStackStackForward(nd);
            if (currentTrace.contains(nd.getCalleeNode())){
                listener.loopDetected(FindSupertypeResultsDgq.this);
            } else {
                nd.getCalleeNode().accept(this);
            }
            // continuing past [call] nodes is done from their corresponding [end]s.
            backStackStack();
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast( nd );
            nd.getNextNode().accept( this );
            currentTrace.removeLast();
        }
        
        @Override
        public void visitImpl(SectionNode nd) throws DataTagsRuntimeException{
            if ( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast( nd );
            pushStackStackForward(nd);
            nd.getStartNode().accept(this);
            backStackStack();
            currentTrace.removeLast();
        }
        
        @Override
        public void visitImpl(PartNode nd) throws DataTagsRuntimeException {
            if( isDebugMode() ) dumpCurrentTrace();
            currentTrace.addLast(nd);
            nd.getStartNode().accept(this);
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            if ( isDebugMode() ) dumpCurrentTrace();
            // either pop the call stack, or end the run and compare the result.
            currentTrace.addLast( nd );
            
            if ( nodeStackStack.peek().isEmpty() ) {
                // END OF A RUN - compare, possibly store trace
                if ( valueStack.peek().isSupersetOf(value) ) {
                    // found!
                    listener.matchFound(FindSupertypeResultsDgq.this);
                } else {
                    listener.nonMatchFound(FindSupertypeResultsDgq.this);
                }
                
            } else {
                // Find out what is the next node to get to. This depends
                // on the stack structure - did we just finished a called part,
                // or was this a section we traversed into.
                List<ThroughNode> poppedStack = popStackStackForward();
                ThroughNode newNodeStackTop = peekStackStack();
                boolean doublePopDone = false;
                if ( (newNodeStackTop != null) 
                        && (newNodeStackTop instanceof CallNode) ) {
                    // double pop: pop the [call] that called the popped [section].
                    poppedStack = popStackStackForward();
                    doublePopDone = true;
                }
                C.head(poppedStack).getNextNode().accept(this);
                backStackStack();
                if ( doublePopDone ) {
                    backStackStack();
                }
            }
            currentTrace.removeLast();        
        }
        
        
        /**
         * Add a new stack to the stack of stacks, where the passed node is at the top
         * of the new stack.
         * <code>
         *  - time ->
         *   n
         *  xx
         * ___ (n= new node)
         * </code>
         * @param aNode 
         */
        private void pushStackStackForward( ThroughNode aNode ) {
            List<ThroughNode> curStack = nodeStackStack.peek();
            List<ThroughNode> newStack = new ArrayList<>(curStack.size()+1);
            newStack.add(aNode);
            newStack.addAll(curStack);
            nodeStackStack.push(newStack);
        }
        
        
        /**
         * Add a new stack to the stack of stacks, which is a popped version of the 
         * currnet stack.
         * <code>
         *  - time ->
         *   y
         *  xxx
         * ____ (returned: [y,x,_])
         * </code>
         * @param aNode 
         */
        private List<ThroughNode> popStackStackForward() {
            List<ThroughNode> popped = nodeStackStack.peek();
            nodeStackStack.push(C.tail(popped));
            return popped;
        }
       
        
        private List<ThroughNode> backStackStack() {
            List<ThroughNode> retVal = nodeStackStack.isEmpty() ? null : nodeStackStack.pop();
            return retVal;
        }
        
        private ThroughNode peekStackStack() {
            List<ThroughNode> currentStack = nodeStackStack.peek();
            return currentStack.isEmpty() ? null : C.head(currentStack);
        }
        
        private void dumpCurrentTrace() {
            System.out.println( currentTrace.stream()
                    .map(n -> n.getId() + ":" + C.last(n.getClass().getCanonicalName().split("\\.")))
                    .collect( joining("/",">",">")));
        }
        
    }

    public boolean isDebugMode() {
        return debugMode;
    }

    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }
    
}
