package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TextNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class GraphvizGraphNodeRefVizalizer extends GraphvizVisualizer {
    
    private final List<InstructionNodeRef> nodeList;
    
    List<String> nodes = new LinkedList<>();
    List<String> edges = new LinkedList<>();
    
    interface NodeRefHandler<T extends NodeRef> {
        void handle( T node, GraphvizGraphNodeRefVizalizer ctxt );
    }
    
    private final Map<Class<?>, NodeRefHandler> handlers = new HashMap<>();
    
    int nextId = 0;
    
    public GraphvizGraphNodeRefVizalizer(List<InstructionNodeRef> nodeList) {
        this.nodeList = nodeList;
        initMap();
    }
    
    @Override
    protected void printBody(BufferedWriter out) throws IOException {
        // TODO implement
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    void vizualizeNodeList( List<InstructionNodeRef> list ) {
        InstructionNodeRef last = null;
        for ( InstructionNodeRef inr : list ) {
            if ( last != null ) {
                edges.add( getNodeId(last) + "->" + getNodeId(inr) );
            }
            
            handlers.get(inr.getClass()).handle(inr, this);
            
            last = inr;
            
        }
    }
    
    void writeTermNode( TermNodeRef node ) {
        nodes.add( getNodeId(node) + "[" + nodeLabel(node, "term\\n" + node.getTerm()) + "]" );
    }
    
    void writeAnswerNode( AnswerNodeRef node ) {
        nodes.add( getNodeId(node) + "[" + nodeLabel(node,"answer") + "]" );
        vizualizeNodeList( node.getImplementation() );
    }
    
    private void initMap() {
        
        addNode( AskNodeRef.class, new NodeRefHandler<AskNodeRef>() {
            @Override
            public void handle(AskNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + "[" + nodeLabel(node, "") +"]" );
                edges.add( getNodeId(node) + "->" + getNodeId( node.getText()) );
                for ( AnswerNodeRef a : node.getAnswers() ) {
                    edges.add( getNodeId(node) + "->" + getNodeId(a) + "[label=\"" + a.getHead().getTitle() + "\"]" );
                }
                for ( TermNodeRef a : node.getTerms()) {
                    edges.add( getNodeId(node) + "->" + getNodeId(a) );
                    writeTermNode(a);
                }
            }
        } );
        
        addNode( CallNodeRef.class, new NodeRefHandler<CallNodeRef>() {
            @Override
            public void handle(CallNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + " [" + nodeLabel(node, "call\\n"+ node.getCalleeId()) +"]" );
            }
        });
        addNode( EndNodeRef.class, new NodeRefHandler<EndNodeRef>(){
            @Override
            public void handle(EndNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + " ["+ nodeLabel(node, "end") + "]");
            }
        } );
        addNode( SetNodeRef.class, new NodeRefHandler<SetNodeRef>() {
            @Override
            public void handle(SetNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + "[" + nodeLabel(node, "set\\n" + node.getSlotNames()) + "]" );
            }
        });
        addNode( TodoNodeRef.class, new NodeRefHandler<TodoNodeRef>() {
            @Override
            public void handle(TodoNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + "[" + nodeLabel(node, "todo") + "]" );
            }
        });
        addNode( TextNodeRef.class, new NodeRefHandler<TextNodeRef>() {
            @Override
            public void handle(TextNodeRef node, GraphvizGraphNodeRefVizalizer ctxt) {
                nodes.add( getNodeId(node) + "[" + nodeLabel(node, "text") + "]" );
            }
        });
        
    }
    
    private <T extends InstructionNodeRef> void addNode( Class<T> clazz, NodeRefHandler<T> hnd ) {
        handlers.put(clazz, hnd);
    }
    
    String nodeLabel( NodeRef n, String extras ) {
        return "label=\">" + getNodeId(n) +"<" + extras + "\"";
    }
    
    String getNodeId( NodeRef nr ) {
        if ( nr.getId() == null ) {
            nr.setId( nextId() );
        }
        return nr.getId();
    }
    
    private String nextId() { return "autoId" + (++nextId); }
    
}
