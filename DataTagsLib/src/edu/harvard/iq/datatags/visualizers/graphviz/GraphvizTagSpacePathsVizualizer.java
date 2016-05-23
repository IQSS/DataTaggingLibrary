package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvEdge.edge;
import static edu.harvard.iq.datatags.visualizers.graphviz.GvNode.node;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Visualizes a tag space based on the qualified names, or paths, of each slot.
 * @author michael
 */
public class GraphvizTagSpacePathsVizualizer extends GraphvizVisualizer {
	
	private final SlotType topLevel;

	public GraphvizTagSpacePathsVizualizer(SlotType topLevel) {
		this.topLevel = topLevel;
	}

	@Override
	void printHeader(BufferedWriter out) throws IOException {
		super.printHeader(out);
//		out.write("graph [overlap=true ranksep=3]");
		out.newLine();
	}
	
	@Override
	protected void printBody( BufferedWriter out ) throws IOException {
		
		final List<String> nodes = new LinkedList<>();
		final List<String> edges = new LinkedList<>();
		
		SlotType.Visitor typePainter = new SlotType.VoidVisitor(){

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
                    edges.add( sTypeName + ":n -> " + sValue + "[ dir=\"back\" style=\"dashed\"]"  );
                });
			}

			@Override
			public void visitCompoundSlotImpl(CompoundSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[shape=\"point\"]");
                t.getFieldTypes().forEach((subFieldType) -> {
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
                sb.append("<TABLE border=\"1\" cellborder=\"0\" cellspacing=\"0\" cellpadding=\"4\"><TR>");
                sb.append("<TD>one of:</TD>");
                int startRGB[] = {200,200,255};
                int endRGB[] = {255,80,80};
                int step=0;
                for ( AtomicValue at:t.values() ) {
                    String htmlCol = "#"+ mix(startRGB[0], endRGB[0], step, t.values().size())
                                        + mix(startRGB[1], endRGB[1], step, t.values().size())
                                        + mix(startRGB[2], endRGB[2], step, t.values().size());
                    sb.append("<TD BGCOLOR=\"").append(htmlCol).append("\">").append(at.getName()).append("</TD>");
                    step++;
                 }
                sb.append("</TR></TABLE>");
                
                return sb.toString();
            }
            
            private String mix( int s, int e, int step, int stepCount ) {
                String retVal = Integer.toHexString( ((stepCount-step)*s + step*e)/stepCount );
                return retVal.length()<2 ? "0"+retVal : retVal;
            }
		};

		topLevel.accept(typePainter);
		for ( String s : nodes ) {
			out.write(s);
			out.newLine();
		}
		for ( String s : edges ) {
			out.write(s);
			out.newLine();
		}
        out.write( node("start")
                .fillColor("transparent")
                .label("DataTags")
                .shape(GvNode.Shape.none)
                .fontColor("#008800")
                .fontSize(16)
                .gv() );
        out.write( edge("start", "DataTags")
                    .color("#008800")
                    .penwidth(4)
                    .gv());
        out.write("{rank=source; start}");
	}
	
}
