package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashSet;
import java.util.Set;

/**
 * Finds all the nodes reachable from the accepting node.
 * @author michael
 */
public class ReachableNodesCollector extends Node.VoidVisitor {
    
    final Set<Node> collection = new HashSet<>();
    
    public Set<Node> getCollectedNodes() {
        return collection;
    }
    
    @Override
    public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        
        collection.add( nd );
        nd.getAnswers().forEach(a-> nd.getNodeFor(a).accept(this));
        if ( nd.getElseNode() != null ) {
            nd.getElseNode().accept(this);
        }
    }
    
    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        collection.add( nd );
        nd.getAnswers().forEach(a-> nd.getNodeFor(a).accept(this));
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        collection.add(nd);
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        collection.add(nd);
    }
    
    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        visitThroughNode(nd);
        if ( nd.getCalleeNode() != null ) {
            // initially, callee nodes may be null, e.g. before linkage.
            nd.getCalleeNode().accept(this);
        }
    }

    @Override
    public void visitImpl(SectionNode nd) throws DataTagsRuntimeException {
        if ( collection.contains(nd) ) return;
        nd.getStartNode().accept(this);
        visitThroughNode(nd);
    }
    
    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        visitThroughNode(nd);
    }

    @Override
    public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
        visitThroughNode(nd);
    }

    void visitThroughNode( ThroughNode nd ) {
        if ( collection.contains(nd) ) return;
        collection.add(nd);
        if ( nd.getNextNode() != null ) {
            nd.getNextNode().accept(this);
        }
    }
    
}
