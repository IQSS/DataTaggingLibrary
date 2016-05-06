package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds all the nodes reachable from the accepting node.
 * @author michael
 */
public class ReachableNodesCollector extends Node.VoidVisitor {
    final Set<Node> collection = new HashSet<>();
    
    
    public Set<Node> getCollection() {
        return collection;
    }
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        collection.add( nd );
        for ( ConsiderAnswer a : nd.getAnswers() ) {
            nd.getNodeFor(a).accept(this);
        }
        nd.getElseNode().accept(this);
    }
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        collection.add( nd );
        for ( Answer a : nd.getAnswers() ) {
            nd.getNodeFor(a).accept(this);
        }
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        visitThroughNode(nd);
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        collection.add(nd);
    }

    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        visitThroughNode(nd);
    }

    @Override
    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
        visitThroughNode(nd);
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        collection.add(nd);
    }
    
    void visitThroughNode( ThroughNode nd ) {
        collection.add(nd);
        nd.getNextNode().accept(this);
    }
}
