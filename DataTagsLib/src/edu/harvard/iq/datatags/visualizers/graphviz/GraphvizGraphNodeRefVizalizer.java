package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class GraphvizGraphNodeRefVizalizer extends GraphvizVisualizer {
    
    private final List<InstructionNodeRef> nodeList;
    
    List<String> nodes = new LinkedList<>();
    List<String> edges = new LinkedList<>();
    
    interface NodeRefHandler<T extends NodeRef> {
        void handle( T node, int depth );
    }
    
    private final Map<Class<?>, NodeRefHandler> handlers = new HashMap<>();
    
    int nextId = 0;
    
    public GraphvizGraphNodeRefVizalizer(List<InstructionNodeRef> aNodeList) {
        nodeList = aNodeList;
        initMap();
        setChartName("ParsedChart");
    }
    
    
    
    @Override
    protected void printBody(BufferedWriter out) throws IOException {
        visualizeNodeList(nodeList, 0);
        for ( String node : nodes ) {
            out.write(node);
            out.newLine();
        }
        out.newLine();
        for ( String edge : edges ) {
            out.write(edge);
            out.newLine();
        }
    }
    
    void visualizeNodeList( List<? extends InstructionNodeRef> list, int depth ) {
        InstructionNodeRef last = null;
        for ( InstructionNodeRef inr : list ) {
            if ( last != null ) {
				String baseEdgeString = "[label=\"ast_next\", color=\"#AAAABB\"";
				if ( depth == 0 ) baseEdgeString = baseEdgeString + ", constraint=false";
                edges.add( sanitizeId(getNodeId(last)) + "->" + sanitizeId(getNodeId(inr)) + baseEdgeString + "]");
            }
            
            handlers.get(inr.getClass()).handle(inr, depth+1);
            
            last = inr;
            
        }
    }
    
    void writeTermNode( TermNodeRef node ) {
        nodes.add( node(getNodeId(node))
                    .label( nodeLabel(node, "term\\n" + node.getExplanation()))
                    .fillColor("#BBBB22")
                    .fontSize(9)
					.shape(GvNode.Shape.tab)
                    .gv() );
    }
    
    void writeAnswerNode( AnswerNodeRef node, int depth ) {
        nodes.add( node(getNodeId(node)).label(nodeLabel(node,"answer")).gv() );
        if ( ! node.getImplementation().isEmpty() ) {
            edges.add( sanitizeId(getNodeId(node)) + "->" + sanitizeId(getNodeId(node.getImplementation().get(0))) + " [label=\"impl\"]");
        }
        visualizeNodeList( node.getImplementation(), depth);
    }
    
    private void initMap() {
        
        addNode( AskNodeRef.class, new NodeRefHandler<AskNodeRef>() {
            @Override
            public void handle(AskNodeRef node, int depth) {
                nodes.add( node(getNodeId(node))
                        .label(nodeLabel(node, "ask"))
                        .fillColor("#BBBBFF")
                        .gv() );
                
                edges.add( sanitizeId(getNodeId(node)) + "->" + sanitizeId(getNodeId( node.getTextNode())) +"[label=\"text\"]");
                
                nodes.add( node(getNodeId(node.getTextNode())).label(nodeLabel(node.getTextNode(),node.getTextNode().getText())).gv() );
                for ( AnswerNodeRef a : node.getAnswers() ) {
                    edges.add( sanitizeId(getNodeId(node)) + "->" + sanitizeId(getNodeId(a)) + "[label=\"" + a.getAnswerText() + "\"]" );
                    writeAnswerNode(a, depth);
                }
                for ( TermNodeRef a : node.getTerms()) {
                    edges.add( sanitizeId(getNodeId(node)) + "->" + sanitizeId(getNodeId(a)) + "[label=\""+a.getTerm()+"\"]" );
                    writeTermNode(a);
                }
                
            }
        } );
        
        addNode( CallNodeRef.class, new NodeRefHandler<CallNodeRef>() {
            @Override
            public void handle(CallNodeRef node, int depth) {
                nodes.add( node(getNodeId(node))
						.shape( GvNode.Shape.cds )
						.fillColor("#BBDDFF")
						.label(nodeLabel(node, "call\\n"+ node.getCalleeId())).gv() );
            }
        });
        addNode( EndNodeRef.class, new NodeRefHandler<EndNodeRef>(){
            @Override
            public void handle(EndNodeRef node, int depth) {
                nodes.add( node(getNodeId(node)).label(nodeLabel(node, "end")).shape(GvNode.Shape.point).gv() );
            }
        } );
        addNode( SetNodeRef.class, new NodeRefHandler<SetNodeRef>() {
            @Override
            public void handle(SetNodeRef node, int depth) {
				StringBuilder sb = new StringBuilder();
				for ( String slot: node.getSlotNames() ) {
					sb.append( slot ).append("=").append(node.getValue(slot) )
						.append(" ");
							
				}
                nodes.add( 
						node(getNodeId(node))
						.label(nodeLabel(node, "set\\n" + sb.toString()))
						.shape(GvNode.Shape.box)
						.gv()
				);
            }
        });
        addNode( TodoNodeRef.class, new NodeRefHandler<TodoNodeRef>() {
            @Override
            public void handle(TodoNodeRef node, int depth) {
                nodes.add( node(getNodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note).label(nodeLabel(node, "todo\\n"+node.getTodoText())).gv() );
            }
        });
        
        
    }
    
    private <T extends InstructionNodeRef> void addNode( Class<T> clazz, NodeRefHandler<T> hnd ) {
        handlers.put(clazz, hnd);
    }
    
    String nodeLabel( NodeRef n, String extras ) {
		String nodeId = getNodeId(n);
		return sanitizeTitle( ( nodeId.startsWith("autoId") )
				? extras
				: ">" + nodeId +"<\\n" + extras );
    }
    
    String getNodeId( NodeRef nr ) {
        if ( nr.getId() == null ) {
            nr.setId( nextId() );
        }
        return nr.getId();
    }
    
    private String nextId() { return "$" + (++nextId); }
    
}
