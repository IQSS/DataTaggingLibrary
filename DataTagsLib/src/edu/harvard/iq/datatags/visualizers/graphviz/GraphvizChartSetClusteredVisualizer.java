package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ReachableNodesCollector;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Given a {@link DecisionGraph}, instances of this class create gravphviz files
 * visualizing the decision graph flow.
 * 
 * @author michael
 */
public class GraphvizChartSetClusteredVisualizer extends GraphvizVisualizer {
    
    private final TagValue.Visitor<String> valueNamer = new TagValue.Visitor<String>() {

        @Override
        public String visitToDoValue(ToDoValue v) {
            return v.getInfo();
        }

        @Override
        public String visitSimpleValue(AtomicValue v) {
            return v.getName();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            StringBuilder sb = new StringBuilder();
            sb.append("{");
            v.getValues().forEach( tv -> 
                sb.append( tv.accept(this) ).append(",") );
            if ( ! v.getValues().isEmpty() ) {
                sb.setLength( sb.length()-1 );
            }
            sb.append("}");
            return sb.toString();
        }

        @Override
        public String visitCompoundValue(CompoundValue aThis) {
            StringBuilder sb = new StringBuilder();
            sb.append("[");
            for ( TagType tt : aThis.getTypesWithNonNullValues() ) {
                sb.append( tt.getName() );
                sb.append(":");
                sb.append( aThis.get(tt).accept(this) );
                sb.append( " " );
            }
            if ( ! aThis.getTypesWithNonNullValues().isEmpty() ) {
                sb.setLength( sb.length()-1 );
            }
            sb.append("]");
            return sb.toString();
        }
    };

    /**
     * Finds all the nodes that are the head of a subroutine/section, to the extent we 
     * have those (we currently don't).
     * @param fc the FlowChart we search in
     * @return Set of all nodes to draw subcharts from.
     */
    private Set<Node> findSubchartHeades(DecisionGraph fc) {
        final Set<Node> candidates = new HashSet<>();
        for ( Node n : fc.nodes() ) { candidates.add(n);}
        for ( Node n : fc.nodes() ) {
            if ( candidates.contains(n) ) {
                n.accept( new Node.VoidVisitor(){
                    
                    @Override
                    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                        for ( Answer n : nd.getAnswers() ) {
                            Node answerNode = nd.getNodeFor(n);
                            candidates.remove(answerNode);
                            answerNode.accept(this);
                        }
                    }

                    @Override
                    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {}

                    @Override
                    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                        candidates.remove(nd.getNextNode());
                        nd.getNextNode().accept(this);
                    }

                    @Override
                    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {}
                });
            }
        }
        
        return candidates;
    }
            
	private class NodePainter extends  Node.VoidVisitor {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();
        Set<Node> targets = new HashSet<>();
        
		public void reset() {
			nodes.clear();
			edges.clear();
		}
		
		@Override
		public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if ( nodeText.length() > 140 ) {
                nodeText = nodeText.substring(0,140) + "...";
            }
			nodes.add( node(nodeId(nd))
					.shape(GvNode.Shape.oval)
					.label( idLabel(nd) + "ask\n" + wrap(nodeText) )
					.gv());
			for ( Answer ans : nd.getAnswers() ) {
				edges.add( edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).tailLabel(ans.getAnswerText()).gv() );
                targets.add( nd.getNodeFor(ans));
			}
		}

		@Override
		public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + nd.getCalleeNodeId())
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
			edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
		}
		
        @Override
		public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + "REJECT\n" + wrap(nd.getReason())  )
						.shape(GvNode.Shape.hexagon)
						.fillColor("#FFAAAA")
						.gv() );
		}
		
		@Override
		public void visitImpl(TodoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label(idLabel(node) + "todo\n"+ wrap(node.getTodoText())).gv() );
			
            edges.add( edge(nodeId(node), nodeId(node.getNextNode())).gv() );
            targets.add( node.getNextNode() );
		}
		
		@Override
		public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) )
                    .append("Set\n");
            for ( TagType tt : nd.getTags().getTypesWithNonNullValues() ) {
                label.append( tt.getName() )
                        .append( "=" )
                        .append( nd.getTags().get(tt).accept(valueNamer) )
                        .append("\n");
            }
			nodes.add( node(nodeId(nd))
						.fillColor("#AADDAA")
						.shape(GvNode.Shape.rect)
						.label( label.toString() )
						.gv());
            edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
		}

		@Override
		public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
                        .shape(GvNode.Shape.point)
                        .fontColor("#AAAAAA")
                        .fillColor("#000000")
                        .add("height", "0.2")
                        .add("width", "0.2")
                        .label("x").gv() );
		}
		
        private String idLabel( Node nd ) {
            return nd.getId().startsWith("[#") ? "" : nd.getId()+"\\n";
        }
	}
	
    private DecisionGraph theGraph;
    
    @Override
    void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph " + getDecisionGraphName() + " {");
		out.newLine();
        out.write( "fontname=\"Courier\"" );
		out.newLine();
		out.write("edge [fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write("node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
        out.write( node("start")
                .fillColor("transparent")
                .shape(GvNode.Shape.none)
                .fontColor("#008800")
                .fontSize(16)
                .gv() );
		out.newLine();
	}
    
	@Override
	protected void printBody(BufferedWriter out) throws IOException {
        printChart(theGraph, out);
        out.write( edge("start", nodeId(theGraph.getStart()))
                    .color("#008800")
                    .penwidth(4)
                    .gv());
        out.write("{rank=source; start}");
		out.newLine();
	}
	
    
    
	void printChart( DecisionGraph fc, BufferedWriter wrt ) throws IOException {
		wrt.write( "subgraph cluster_" + sanitizeId(fc.getId()) + " {");
		wrt.newLine();
		wrt.newLine();
		wrt.write( String.format("label=\"%s\"", humanTitle(fc)) );
		wrt.newLine();
		
        // group to subcharts
        Set<Node> subchartHeads = findSubchartHeades( fc );
        List<String> edges = new LinkedList<>();
        for ( Node chartHead :subchartHeads ) {
            System.out.println("Subchart: " + sanitizeId(chartHead.getId()) );
            wrt.write( "subgraph cluster_" + sanitizeId(chartHead.getId()) + " {");
            wrt.newLine();
            wrt.write( String.format("label=\"%s\"; color=\"#AABBDD\"; labeljust=\"l\"", sanitizeTitle(chartHead.getId())) );
            wrt.newLine();
            
            ReachableNodesCollector rnc = new ReachableNodesCollector();
            chartHead.accept(rnc);
            
            NodePainter np = new NodePainter();
            for ( Node n : rnc.getCollection() ) {
                n.accept(np);
            }
            
            for ( String s : np.nodes ) {
                wrt.write(s);
                wrt.newLine();
            }
            edges.addAll( np.edges );
            wrt.newLine();
            wrt.write("}");
            wrt.newLine();
        }
        
        for ( String s : edges ) {
            wrt.write( s );
            wrt.newLine();
        }
        
		wrt.write("}");
		wrt.newLine();
		
	}

	String nodeId( Node nd ) {
		return sanitizeId( nd.getId() );
	}
    
    public void setDecisionGraph( DecisionGraph aDecisionGraph ) {
        theGraph = aDecisionGraph;
    }
    
    public DecisionGraph getDecisionGraph() {
        return theGraph;
    }
}
