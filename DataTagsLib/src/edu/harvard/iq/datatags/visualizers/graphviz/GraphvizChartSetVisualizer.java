package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.util.HashSet;
import java.util.Set;

/**
 * Given a {@link FlowChartSet}, instances of this class create gravphviz files
 * visualizing the chartset flow.
 * 
 * @author michael
 */
public class GraphvizChartSetVisualizer extends GraphvizVisualizer {
	private FlowChartSet chartSet;
    
    private final TagValue.Visitor<String> valueNamer = new TagValue.Visitor<String>() {

        @Override
        public String visitToDoValue(ToDoValue v) {
            return v.getName();
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
            for ( TagType tt : aThis.getSetFieldTypes() ) {
                sb.append( tt.getName() );
                sb.append(":");
                sb.append( aThis.get(tt).accept(this) );
                sb.append( " " );
            }
            sb.append("]");
            return sb.toString();
        }
    };
            
	private class NodePainter implements  Node.Visitor<Void> {
		
		List<String> nodes = new LinkedList<>(), edges = new LinkedList<>();
        Set<Node> targets = new HashSet<>();
        
		public void reset() {
			nodes.clear();
			edges.clear();
		}
		
		@Override
		public Void visitAskNode(AskNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd))
					.shape(GvNode.Shape.oval)
					.label( idLabel(nd) + "ask\\n" + nd.getText() )
					.gv());
			for ( Answer ans : nd.getAnswers() ) {
				edges.add( edge(nodeId(nd), nodeId(nd.getNodeFor(ans))).tailLabel(ans.getAnswerText()).gv() );
                targets.add( nd.getNodeFor(ans));
			}

			return null;
		}

		@Override
		public Void visitCallNode(CallNode nd) throws DataTagsRuntimeException {
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
		public Void visitTodoNode(TodoNode node) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(node))
							.fillColor("#AAFFAA")
							.shape(GvNode.Shape.note)
							.label(idLabel(node) + "todo\\n"+node.getTodoText()).gv() );
			
            edges.add( edge(nodeId(node), nodeId(node.getNextNode())).gv() );
            targets.add( node.getNextNode() );
			return null;
		}
		
		@Override
		public Void visitSetNode(SetNode nd) throws DataTagsRuntimeException {
            StringBuilder label = new StringBuilder();
            label.append( idLabel(nd) )
                    .append("Set\\n");
            for ( TagType tt : nd.getTags().getSetFieldTypes() ) {
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
		public Void visitEndNode(EndNode nd) throws DataTagsRuntimeException {
			nodes.add( node(nodeId(nd)).shape(GvNode.Shape.point)
                            .fillColor("#333333").add("height", "0.25").add("width", "0.25").gv() );
			return null;
		}
		
        private String idLabel( Node nd ) {
            return nd.getId().startsWith("$") ? "" : ">" + nodeId(nd) + "< ";
        }
	}
	
	private final NodePainter nodePainter = new NodePainter();
	
    @Override
    void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph " + getChartName() + " {");
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
		for (String s : nodePainter.edges) {
			out.write(s);
			out.newLine();
		}
        out.write("{rank=source; start}");
		out.newLine();
	}
	
	void printChart( FlowChart fc, BufferedWriter wrt ) throws IOException {
		wrt.write( "subgraph cluster_" + sanitizeId(fc.getId()) + " {");
		wrt.newLine();

		wrt.write( String.format("label=\"%s\"", humanTitle(fc)) );
		wrt.newLine();
		
        Set<Node> sources = new HashSet<>();
		for ( Node n : fc.nodes() ) {
            sources.add(n);
			try {
				n.accept( nodePainter );
			} catch (DataTagsRuntimeException ex) {
				Logger.getLogger(GraphvizChartSetVisualizer.class.getName()).log(Level.SEVERE, null, ex);
			}
		}
		
		for ( String s : nodePainter.nodes ) {
			wrt.write(s);
			wrt.newLine();
		}
		
        sources.removeAll( nodePainter.targets );
        wrt.write("{ rank=same; " );
        boolean first = true;
        for ( Node n : sources ) {
            if ( first ) {
                first = false;
            } else {
                wrt.write(", "); 
            }
            wrt.write( GvObject.sanitizeId(nodeId(n)) );
        }
		wrt.write("}");
		wrt.newLine();
        
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
