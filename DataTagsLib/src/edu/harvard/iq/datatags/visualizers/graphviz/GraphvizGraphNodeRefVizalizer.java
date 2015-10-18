package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTermSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
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
    
    private final List<AstNode> nodeList;
    
    List<String> nodes = new LinkedList<>();
    List<String> edges = new LinkedList<>();
    
    interface AstNodeHandler<T extends AstNode> {
        void handle( T node, int depth );
    }
    
    private final Map<Class<?>, AstNodeHandler> handlers = new HashMap<>();
    
    int nextId = 0;
    
    public GraphvizGraphNodeRefVizalizer(List<AstNode> aNodeList) {
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
    
    void visualizeNodeList( List<? extends AstNode> list, int depth ) {
        AstNode last = null;
        for ( AstNode inr : list ) {
            if ( last != null ) {
                edges.add( edge(getNodeId(last), getNodeId(inr) )
                            .color("#AAAABB")
                            .label("ast_next")
                            .constraint( depth!=0 )
                            .gv() );
            }
            
            handlers.get(inr.getClass()).handle(inr, depth+1);
            
            last = inr;
            
        }
    }
    
    void writeTermNode( AstTermSubNode node ) {
        nodes.add( node(getNodeId(node))
                    .label( nodeLabel(node, "term\\n" + node.getExplanation()))
                    .fillColor("#BBBB22")
                    .fontSize(9)
					.shape(GvNode.Shape.tab)
                    .gv() );
    }
    
    void writeAnswerNode( AstAnswerSubNode node, int depth ) {
        nodes.add( node(getNodeId(node)).label(nodeLabel(node,"answer")).gv() );
        if ( ! node.getSubGraph().isEmpty() ) {
            edges.add( edge(getNodeId(node), getNodeId(node.getSubGraph().get(0))).label("impl").gv());
        }
        visualizeNodeList( node.getSubGraph(), depth);
    }
    
    private void initMap() {
        
        addNode(AstAskNode.class, new AstNodeHandler<AstAskNode>() {
            @Override
            public void handle(AstAskNode node, int depth) {
                nodes.add( node(getNodeId(node))
                        .label(nodeLabel(node, "ask"))
                        .fillColor("#BBBBFF")
                        .gv() );
                
                edges.add( edge(getNodeId(node),getNodeId(node.getTextNode())).label("text").gv());
                
                nodes.add( node(getNodeId(node.getTextNode())).label(nodeLabel(node.getTextNode(),node.getTextNode().getText())).gv() );
                for ( AstAnswerSubNode a : node.getAnswers() ) {
                    edges.add( edge(getNodeId(node),getNodeId(a)).label(a.getAnswerText()).gv()) ;
                    writeAnswerNode(a, depth);
                }
                for ( AstTermSubNode a : node.getTerms()) {
                    edges.add( edge(getNodeId(node), getNodeId(a)).label(a.getTerm()).gv() );
                    writeTermNode(a);
                }
                
            }
        } );
        
        addNode(AstCallNode.class, new AstNodeHandler<AstCallNode>() {
            @Override
            public void handle(AstCallNode node, int depth) {
                nodes.add( node(getNodeId(node))
						.shape( GvNode.Shape.cds )
						.fillColor("#BBDDFF")
						.label(nodeLabel(node, "call\\n"+ node.getCalleeId())).gv() );
            }
        });
        
        addNode(AstEndNode.class, new AstNodeHandler<AstEndNode>(){
            @Override
            public void handle(AstEndNode node, int depth) {
                nodes.add( node(getNodeId(node)).label(nodeLabel(node, "end")).shape(GvNode.Shape.point).gv() );
            }
        } );
        
        addNode(AstSetNode.class, new AstNodeHandler<AstSetNode>() {
            @Override
            public void handle(AstSetNode node, int depth) {
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
        addNode(AstTodoNode.class, new AstNodeHandler<AstTodoNode>() {
            @Override
            public void handle(AstTodoNode node, int depth) {
                nodes.add( node(getNodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
                            .label(nodeLabel(node, "todo\\n"+node.getTodoText())).gv() );
            }
        });
        
        addNode(AstRejectNode.class, new AstNodeHandler<AstRejectNode>() {
            @Override
            public void handle(AstRejectNode node, int depth) {
                nodes.add( node(getNodeId(node))
							.fillColor("#FFAAAA")
							.shape(GvNode.Shape.hexagon).label(nodeLabel(node, "reject\\n"+node.getReason())).gv() );
            }
        });
        
        
    }
    
    private <T extends AstNode> void addNode( Class<T> clazz, AstNodeHandler<T> hnd ) {
        handlers.put(clazz, hnd);
    }
    
    String nodeLabel( AstNode n, String extras ) {
		String nodeId = getNodeId(n);
		return wrap( sanitizeTitle( ( nodeId.startsWith("$") )
                    ? extras
                    : ">" + nodeId +"<\\n" + extras ));
    }
    
    String getNodeId( AstNode nr ) {
        if ( nr.getId() == null ) {
            nr.setId( nextId() );
        }
        return nr.getId();
    }
    
    private String nextId() { return "$" + (++nextId); }
    
}
