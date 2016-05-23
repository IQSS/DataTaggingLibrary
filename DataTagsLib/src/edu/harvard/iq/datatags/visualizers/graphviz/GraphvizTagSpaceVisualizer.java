package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.types.ToDoSlot;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import java.io.BufferedWriter;
import java.io.IOException;
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
	void printHeader(BufferedWriter out) throws IOException {
		super.printHeader(out);
		out.write("graph [overlap=true ranksep=3]");
		out.newLine();
	}
	
	@Override
	protected void printBody( BufferedWriter out ) throws IOException {
		
		final List<String> nodes = new LinkedList<>();
		final List<String> edges = new LinkedList<>();
		
		SlotType.Visitor typePainter = new SlotType.Visitor<Void>(){

			@Override
			public Void visitSimpleSlot(AtomicSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\"]");
				for ( AtomicValue val : t.values() ) {
					String sValue = sTypeName+"_"+sanitizeId( val.getName() );
					nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
					edges.add( sTypeName + " -> " + sValue );
				}
				return null;
			}

			@Override
			public Void visitAggregateSlot(AggregateSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\" peripheries=\"2\"]");
				for ( AtomicValue val : t.getItemType().values() ) {
					String sValue = sTypeName+"_"+sanitizeId( val.getName() );
					nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
					edges.add( sTypeName + " -> " + sValue );
				}
				return null;
			}

			@Override
			public Void visitCompoundSlot(CompoundSlot t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"octagon\" peripheries=\"2\"]");
				for ( SlotType val : t.getFieldTypes() ) {
					edges.add( sTypeName + " -> " + sanitizeId(val.getName()) );
					val.accept(this);
				}
				return null;
			}

			@Override
			public Void visitTodoSlot(ToDoSlot t) {
				nodes.add( sanitizeId(t.getName()) + "[label=\""+t.getName()+" (todo)\" shape=\"plaintext\" peripheries=\"2\"]");
				return null;
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
	
	}
	
}
