package edu.harvard.iq.datatags.tools.processors;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ToDoNode;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.ConsiderOption;
import edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode;
import edu.harvard.iq.datatags.model.graphs.nodes.PartNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SectionNode;
import edu.harvard.iq.datatags.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

/**
 * An optimizer that makes the chart use a single end node, instead
 * of many. Only end nodes that don't have an ID are optimized.
 * 
 * @author michael
 */
public class EndNodeOptimizer implements DecisionGraphProcessor {

    @Override
    public String getTitle() {
        return "Unifying (end) nodes.";
    }

    @Override
    public DecisionGraph process(final DecisionGraph fcs) {
        
        // create the end node
        EndNode tempEnd = (EndNode) fcs.getNode(DecisionGraphCompiler.SYNTHETIC_END_NODE_ID);
        if ( tempEnd == null ) {
            tempEnd = new EndNode(DecisionGraphCompiler.SYNTHETIC_END_NODE_ID);
            fcs.add( tempEnd );
        }
        final EndNode end = tempEnd;
        final List<Node> toRemove = new ArrayList<>();
        
        // now traverse the chart and replace all.
        Node.VoidVisitor traversor = new Node.VoidVisitor() {
           
            @Override
            public void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException {
                for ( ConsiderOption a : nd.getAnswers() ) {
                    Node ansNode = nd.getNodeFor(a);
                    if ( shouldReplace(ansNode) ) {
                        fcs.remove(ansNode);
                        nd.setNodeFor(a, end);
                    }
                }
            }    
            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                for ( Answer a : nd.getAnswers() ) {
                    Node ansNode = nd.getNodeFor(a);
                    if ( shouldReplace(ansNode) ) {
                        fcs.remove(ansNode);
                        nd.setNodeForAnswer(a, end);
                    }
                }
            }

            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }


            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }

            @Override
            public void visitImpl(ToDoNode nd) throws DataTagsRuntimeException {
                visitThroughNode(nd);
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {}
            
            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                if ( AstNodeIdProvider.isAutoId(nd.getId()) ) {
                    toRemove.add(nd);
                }
            }
            
            @Override 
            public void visitImpl(SectionNode nd) throws DataTagsRuntimeException{
                visitThroughNode(nd);
            } 
            
            @Override
            public void visitImpl(PartNode nd) throws DataTagsRuntimeException{}
            
            private void visitThroughNode( ThroughNode nd) {
                if ( shouldReplace(nd.getNextNode()) ) {
                    fcs.remove(nd.getNextNode());
                    nd.setNextNode( end );
                }
            }
            
            private boolean shouldReplace( Node n ) {
                return ( ( n instanceof EndNode ) && AstNodeIdProvider.isAutoId(n.getId()));
            }
        };
        
        // We need to hold the node list to avoid concurrent modification errors.
        Set<String> nodeIds = new TreeSet<>();
        for ( Node n : fcs.nodes() ) {
            nodeIds.add( n.getId() );
        }
        
        for ( String nodeId : nodeIds ) {
            Node n = fcs.getNode(nodeId);
            if ( n != null ) {
                n.accept(traversor);
            }
        }
        
        toRemove.forEach( nd -> fcs.remove(nd) );
        
        return fcs;
    }
    
}
