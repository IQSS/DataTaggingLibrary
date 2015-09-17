package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.util.ReachableNodesCollector;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Given a {@link FlowChartSet}, instances of this class create gravphviz files
 * visualizing the chartset flow.
 * 
 * @author michael
 */
public class GraphvizChartSetClusteredVisualizer extends GraphvizVisualizer {
	private FlowChartSet chartSet;
    
    private final TagValue.Visitor<String> valueNamer = new TagValue.Visitor<String>() {

        @Override
        public String visitToDoValue(ToDoValue v) {
            return v.getInfo();
        }

        @Override
        public String visitSimpleValue(SimpleValue v) {
            return v.getName();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            for ( TagValue tv : v.getValues() ) {
                sb.append( tv.accept(this) );
                sb.append(" ");
            }
            sb.append("]");
            return sb.toString();
        }

        @Override
        public String visitCompoundValue(CompoundValue aThis) {
            StringBuilder sb = new StringBuilder();
            sb.append("[ ");
            for ( TagType tt : aThis.getTypesWithNonNullValues() ) {
                sb.append( tt.getName() );
                sb.append(":");
                sb.append( aThis.get(tt).accept(this) );
                sb.append( " " );
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
    private Set<Node> findSubchartHeades(FlowChart fc) {
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
            
	private class NodePainter implements  Node.Visitor<Void> {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();
        Set<Node> targets = new HashSet<>();
        
		public void reset() {
			nodes.clear();
			edges.clear();
		}
		
		@Override
		public Void visit(AskNode nd) throws DataTagsRuntimeException {
            String nodeText = nd.getText();
            if ( nodeText.length() > 140 ) {
                nodeText = nodeText.substring(0,140) + "...";
            }
			nodes.add( node(nodeId(nd))
					.shape(GvNode.Shape.oval)
					.label( idLabel(nd) + "ask\\n" + wrap(nodeText) )
					.gv());
			for ( Answer ans : nd.getAnswers() ) {
				edges.add( edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).tailLabel(ans.getAnswerText()).gv() );
                targets.add( nd.getNodeFor(ans));
			}

			return null;
		}

		@Override
		public Void visit(CallNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + nd.getCalleeChartId() +"/" + nd.getCalleeNodeId())
						.shape(GvNode.Shape.cds)
						.fillColor("#BBBBFF")
						.gv() );
			edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
			return null;
		}
		
        @Override
		public Void visit(RejectNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
						.label( idLabel(nd) + "REJECT\\n" + wrap(nd.getReason())  )
						.shape(GvNode.Shape.hexagon)
						.fillColor("#FFAAAA")
						.gv() );
			return null;
		}
		
		@Override
		public Void visit(TodoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label(idLabel(node) + "todo\\n"+ wrap(node.getTodoText())).gv() );
			
            edges.add( edge(nodeId(node), nodeId(node.getNextNode())).gv() );
            targets.add( node.getNextNode() );
			return null;
		}
		
		@Override
		public Void visit(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) )
                    .append("Set\\n");
            for ( TagType tt : nd.getTags().getTypesWithNonNullValues() ) {
                label.append( tt.getName() )
                        .append( "=" )
                        .append( nd.getTags().get(tt).accept(valueNamer) )
                        .append("\\n");
            }
			nodes.add( node(nodeId(nd))
						.fillColor("#AADDAA")
						.shape(GvNode.Shape.rect)
						.label( label.toString() )
						.gv());
            edges.add( edge(nodeId(nd), nodeId(nd.getNextNode())).gv() );
            targets.add( nd.getNextNode() );
			return null;
		}

		@Override
		public Void visit(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd)).shape(GvNode.Shape.point)
                            .fillColor("#333333").add("height", "0.2").add("width", "0.2").gv() );
			return null;
		}
		
        private String idLabel( Node nd ) {
            return nd.getId().startsWith("$") ? "" : nd.getId()+"\\n";// ">" + nodeId(nd) + "< ";
        }
	}
	
    @Override
    void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph " + getChartName() + " {");
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
		for (FlowChart fc : chartSet.charts()) {
			printChart(fc, out);
            out.write( edge("start", nodeId(fc.getStart()))
                        .color("#008800")
                        .penwidth(4)
                        .gv());
		}
        out.write("{rank=source; start}");
		out.newLine();
	}
	
    
    
	void printChart( FlowChart fc, BufferedWriter wrt ) throws IOException {
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
		return nodeId( nd.getChart() != null ? nd.getChart().getId() : "chart_is_null", nd.getId() );
	}
	
	String nodeId( String chartId, String nodeId ) {
		return chartId + "#" + nodeId;
	}
	
	public FlowChartSet getChartSet() {
		return chartSet;
	}

	public void setChartSet(FlowChartSet chartSet) {
		this.chartSet = chartSet;
	}
	
}
