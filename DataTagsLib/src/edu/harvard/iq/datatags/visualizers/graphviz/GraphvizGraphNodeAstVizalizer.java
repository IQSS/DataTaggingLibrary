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
public class GraphvizGraphNodeAstVizalizer extends GraphvizVisualizer {
    
    private final List<? extends AstNode> nodeList;
    
    List<String> nodes = new LinkedList<>();
    List<String> edges = new LinkedList<>();
    
    interface AstNodeHandler<T extends AstNode> {
        void handle( T node, int depth );
    }
    
    private final Map<Class<?>, AstNodeHandler> handlers = new HashMap<>();
    
    int nextId = 0;
    
    public GraphvizGraphNodeAstVizalizer(List<? extends AstNode> aNodeList) {
        nodeList = aNodeList;
        initMap();
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
                edges.add( edge(last.getId(), inr.getId() )
                            .color("#AAAABB")
                            .label("ast_next")
                            .constraint( depth!=0 )
                            .gv() );
            }
            
            handlers.get(inr.getClass()).handle(inr, depth+1);
            
            last = inr;
            
        }
    }
    
    void writeTermNode( String termNodeId, AstTermSubNode node ) {
        nodes.add( node(termNodeId)
                    .label( nodeLabel(null, "term\\n" + node.getExplanation()))
                    .fillColor("#BBBB22")
                    .fontSize(9)
					.shape(GvNode.Shape.tab)
                    .gv() );
    }
    
    void writeAnswerNode( String nodeId, String ansNodeId, AstAnswerSubNode node, int depth ) {
        nodes.add(node(ansNodeId).label(nodeLabel(null,"answer")).gv() );
        if ( ! node.getSubGraph().isEmpty() ) {
            edges.add( edge(nodeId, node.getSubGraph().get(0).getId()).label("impl").gv() );
        }
        visualizeNodeList( node.getSubGraph(), depth);
    }
    
    private void initMap() {
        
        setNodeTypeHandler(AstAskNode.class, (AstAskNode node, int depth) -> {
            nodes.add( node(node.getId())
                    .label(nodeLabel(node.getId(), "ask"))
                    .fillColor("#BBBBFF")
                    .gv() );
            
            edges.add( edge(node.getId(),node.getId()+"$TEXT").label("text").gv());
            
            nodes.add( node(node.getId()+"$TEXT").label(nodeLabel(null,node.getTextNode().getText())).gv() );
            
            node.getAnswers().forEach( a -> {
                String ansNodeId = node.getId() + "#ANS#" + sanitizeId(a.getAnswerText());
                edges.add( edge(node.getId(),ansNodeId).label(a.getAnswerText()).gv()) ;
                writeAnswerNode(node.getId(), ansNodeId, a, depth);
            });
            
            node.getTerms().forEach( tsn -> {
                String termNodeId = node.getId() + "#TERM#" + sanitizeId( tsn.getTerm() );
                edges.add( edge(node.getId(), termNodeId).label(tsn.getTerm()).gv() );
                writeTermNode( termNodeId, tsn);
            });
        });
        
        setNodeTypeHandler(AstCallNode.class, (AstCallNode node, int depth) -> {
            nodes.add( node(node.getId())
                    .shape( GvNode.Shape.cds )
                    .fillColor("#BBDDFF")
                    .label( nodeLabel(node.getId(), "call\\n"+ node.getCalleeId())).gv() );
        });
        
        setNodeTypeHandler(AstEndNode.class, (AstEndNode node, int depth) -> {
            nodes.add( node(node.getId()).label(nodeLabel(node.getId(), "end")).shape(GvNode.Shape.point).gv() );
        });
        
        setNodeTypeHandler(AstSetNode.class, (AstSetNode node, int depth) -> {
            final StringBuilder sb = new StringBuilder();
            AstSetNode.Assignment.Visitor asgnmntPainter = new AstSetNode.Assignment.Visitor() {
                @Override
                public void visit(AstSetNode.AtomicAssignment aa) {
                    sb.append(aa.getSlot()).append("=").append(aa.getValue());
                }
                
                @Override
                public void visit(AstSetNode.AggregateAssignment aa) {
                    sb.append(aa.getSlot()).append("+={").append(aa.getValue()).append("}");
                }
            };
            node.getAssignments().forEach( a->a.accept(asgnmntPainter));
            
            nodes.add(
                    node(node.getId())
                            .label(nodeLabel(node.getId(), "set\\n" + sb.toString()))
                            .shape(GvNode.Shape.box)
                            .gv()
            );
        });
        setNodeTypeHandler(AstTodoNode.class, (AstTodoNode node, int depth) -> {
            nodes.add( node(node.getId())
                    .fillColor("#AAFFAA")
                    .shape(GvNode.Shape.note)
                    .label(nodeLabel(node.getId(), "todo\\n"+node.getTodoText())).gv() );
        });
        
        setNodeTypeHandler(AstRejectNode.class, (AstRejectNode node, int depth) -> {
            nodes.add( node(node.getId())
                    .fillColor("#FFAAAA")
                    .shape(GvNode.Shape.hexagon).label(nodeLabel(node.getId(), "reject\\n"+node.getReason())).gv() );
        });
        
        
    }
    
    private <T extends AstNode> void setNodeTypeHandler( Class<T> clazz, AstNodeHandler<T> hnd ) {
        handlers.put(clazz, hnd);
    }
    
    String nodeLabel( String nodeId, String extras ) {
		return wrap( sanitizeTitle( ( nodeId==null || nodeId.startsWith("[#") )
                    ? extras
                    : ">" + nodeId +"<\\n" + extras ));
    }
    
    
}
