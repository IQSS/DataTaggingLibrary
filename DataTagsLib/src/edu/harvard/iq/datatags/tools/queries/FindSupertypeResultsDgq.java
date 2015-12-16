package edu.harvard.iq.datatags.tools.queries;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

/**
 * Gets a decision graph and a {@link CompoundValue}, and returns all the runs
 * that result in this value, or in a value that is a superset of this value.
 * 
 * The "Dgq" in the class name stand for "Decision Graph Query". We'll have more of those.
 * @author michael
 */
public class FindSupertypeResultsDgq {
    private final DecisionGraph subject;
    private final CompoundValue value;
    
    public FindSupertypeResultsDgq( DecisionGraph aDecisionGraph, CompoundValue aValue ) {
        subject = aDecisionGraph;
        value = aValue;
    }
    
    public Set<RunTrace> get() {
        Set<RunTrace> traces = new HashSet<>();
        subject.getStart().accept( new GraphTraverser() );
        
        return traces;
    }
    
    class GraphTraverser extends Node.VoidVisitor {
        
        LinkedList<Node> currentTrace = new LinkedList<>();
        LinkedList<Answer> currentAnswers = new LinkedList<>();
        Deque<CompoundValue> valueStack = new LinkedList<>();
        
        @Override
        public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            currentTrace.addLast( nd );
            nd.getAnswers().forEach( ans -> {
                currentAnswers.addLast(ans);
                // process answer nodes
                currentAnswers.removeLast();
            });
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            currentTrace.addLast(nd);
            valueStack.push( valueStack.peek().composeWith(nd.getTags()) );
            nd.getNextNode().accept(this);
            valueStack.pop();
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
            // Do nothing - no tags will be generated, so no matching will be done
        }

        @Override
        public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
            currentTrace.addLast( nd );
            nd.getNextNode().accept( this );
            currentTrace.removeLast();
        }

        @Override
        public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
            // either pop the call stack, or end the run and compare the result.
            currentTrace.addLast( nd );
            // compare, possibly store trace
            
            currentTrace.removeLast();        
        }
        
    }
    
}
