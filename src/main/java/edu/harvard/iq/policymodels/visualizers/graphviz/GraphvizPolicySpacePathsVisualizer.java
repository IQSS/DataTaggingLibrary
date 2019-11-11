package edu.harvard.iq.policymodels.visualizers.graphviz;

import edu.harvard.iq.policymodels.model.decisiongraph.nodes.CallNode;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.PartNode;
import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.ToDoSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AtomicValue;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.policymodels.visualizers.graphviz.GvNode.node;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import static java.util.stream.Collectors.joining;

/**
 * Visualizes a tag space based on the qualified names, or paths, of each slot.
 * @author michael
 */
public class GraphvizPolicySpacePathsVisualizer extends GraphvizVisualizer {
	
	private final AbstractSlot topLevel;

	public GraphvizPolicySpacePathsVisualizer(AbstractSlot topLevel) {
		this.topLevel = topLevel;
	}

	@Override
	void printHeader(PrintWriter out) throws IOException {
		super.printHeader(out);
        out.println("graph [concentrate=true]");
		out.println();
	}
	
	@Override
	protected void printBody( PrintWriter out ) throws IOException {
		
		final List<String> nodes = new LinkedList<>();
		final List<String> edges = new LinkedList<>();
		
		AbstractSlot.Visitor typePainter = new AbstractSlot.VoidVisitor(){

			@Override
			public void visitAtomicSlotImpl(AtomicSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[shape=\"none\" fillcolor=\"none\" label=<"+makeHtml(t)+">]");
			}

			@Override
			public void visitAggregateSlotImpl(AggregateSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[shape=\"invtrapezium\" label=\"some of\"]");
                t.getItemType().values().forEach( val -> {
                    String sValue = sTypeName+"_"+sanitizeId( val.getName() );
                    nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"egg\" fillcolor=\"#CCCCFF\"]");
                    edges.add( sTypeName + " -> " + sValue + "[ dir=\"back\" style=\"dashed\"]"  );
                });
			}

			@Override
			public void visitCompoundSlotImpl(CompoundSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[shape=\"point\"]");
                t.getSubSlots().forEach((subFieldType) -> {
                    edges.add( sTypeName + " -> " + sanitizeId(subFieldType.getName()) + ":w [label=\"" + subFieldType.getName() + "\"]" );
                    subFieldType.accept(this);
                });
			}

			@Override
			public void visitTodoSlotImpl(ToDoSlot t) {
				nodes.add( node(sanitizeId(t.getName())).label("TODO").shape(GvNode.Shape.note).fillColor("#AAFFAA").gv());
			}

            private String makeHtml(AtomicSlot t) {
                StringBuilder sb = new StringBuilder();
                
                sb.append("<TABLE border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"4\">");
                
                int totalCharacterCount = t.values().stream().mapToInt(v->v.getName().length()).sum();
                int perRow = (totalCharacterCount>50) ? (int)Math.ceil(Math.sqrt(t.values().size())):Integer.MAX_VALUE;
                
                List<String> cells = new ArrayList<>(t.values().size()+1);
                int startRGB[] = {200,200,255};
                int endRGB[] = {255,80,80};
                int step=0;
                StringBuilder rowSb = new StringBuilder();
                for ( AtomicValue at:t.values() ) {
                    String htmlCol = "#"+ mix(startRGB[0], endRGB[0], step, t.values().size())
                                        + mix(startRGB[1], endRGB[1], step, t.values().size())
                                        + mix(startRGB[2], endRGB[2], step, t.values().size());
                    rowSb.append("<TD BGCOLOR=\"").append(htmlCol).append("\">").append(at.getName()).append("</TD>");
                    step++;
                    if ( step%perRow == 0 ) {
                        cells.add(rowSb.toString());
                        rowSb.setLength(0);
                    }
                 }
                if ( rowSb.length() > 0 ) {
                    cells.add( rowSb.toString() );
                }
                if ( cells.size() == 1 ) {
                    sb.append("<TR><TD>one of:</TD>");
                } else {
                    sb.append("<TR><TD rowspan=\"").append(cells.size()+1).append("\">one of:</TD></TR><TR>");
                }
                sb.append( cells.stream().collect(joining("</TR><TR>")) );
                sb.append( "</TR></TABLE>" );
                
                return sb.toString();
            }
            
            private String mix( int s, int e, int step, int stepCount ) {
                String retVal = Integer.toHexString( ((stepCount-step)*s + step*e)/stepCount );
                return retVal.length()<2 ? "0"+retVal : retVal;
            }
		};

		topLevel.accept(typePainter);
        nodes.forEach((s) -> {
            out.println(s);
        });
        edges.forEach((s) -> {
            out.println(s);
        });
        out.println( node("start")
                .fillColor("transparent")
                .label(sanitizeId(topLevel.getName()))
                .shape(GvNode.Shape.none)
                .fontColor("#008800")
                .fontSize(16)
                .gv() );
        out.println( edge("start", sanitizeId(topLevel.getName()))
                    .color("#008800")
                    .penwidth(4)
                    .gv());
        out.println("{rank=source; start}");
	}

}
