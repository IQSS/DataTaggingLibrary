package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

/**
 * Visualizes a {@link SlotType} as a tree.
 * @author michael
 */
public class GraphvizTagSpaceVisualizer extends GraphvizVisualizer {
	
	private final SlotType topLevel;

	public GraphvizTagSpaceVisualizer(SlotType topLevel) {
		this.topLevel = topLevel;
	}

	@Override
	void printHeader(PrintWriter out) throws IOException {
		super.printHeader(out);
		out.println("graph [overlap=true ranksep=3]");
	}
	
	@Override
	protected void printBody( PrintWriter out ) throws IOException {
		
		final List<String> nodes = new LinkedList<>();
		final List<String> edges = new LinkedList<>();
		
		SlotType.Visitor typePainter = new SlotType.Visitor<Void>(){

			@Override
			public Void visitSimpleSlot(AtomicSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\"]");
                t.values().forEach( val -> {
                    String sValue = sTypeName+"_"+sanitizeId( val.getName() );
                    nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
                    edges.add( sTypeName + " -> " + sValue );
                });
				return null;
			}

			@Override
			public Void visitAggregateSlot(AggregateSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\" peripheries=\"2\"]");
                t.getItemType().values().forEach( val -> {
                    String sValue = sTypeName+"_"+sanitizeId( val.getName() );
                    nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
                    edges.add( sTypeName + " -> " + sValue );
                });
				return null;
			}

			@Override
			public Void visitCompoundSlot(CompoundSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"octagon\" peripheries=\"2\"]");
                t.getFieldTypes().forEach( val  -> {
                    edges.add( sTypeName + " -> " + sanitizeId(val.getName()) );
                    val.accept(this);
                });
				return null;
			}

			@Override
			public Void visitTodoSlot(ToDoSlot t) {
				nodes.add( sanitizeId(t.getName()) + "[label=\""+t.getName()+" (todo)\" shape=\"plaintext\" peripheries=\"2\"]");
				return null;
			}
		};

		topLevel.accept(typePainter);
        nodes.forEach((s) -> {
            out.println(s);
        });
        edges.forEach((s) -> {
            out.println(s);
        });
	
	}
	
}
