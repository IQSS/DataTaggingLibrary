package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.SimpleType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.ToDoType;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author michael
 */
public class GraphvizDataStructureVisualizer extends GraphvizVisualizer {
	
	private final TagType topLevel;

	public GraphvizDataStructureVisualizer(TagType topLevel) {
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
		
		TagType.Visitor typePainter = new TagType.Visitor<Void>(){

			@Override
			public Void visitSimpleType(SimpleType t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\"]");
				for ( TagValue val : t.values() ) {
					String sValue = sTypeName+"_"+sanitizeId( val.getName() );
					nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
					edges.add( sTypeName + " -> " + sValue );
				}
				return null;
			}

			@Override
			public Void visitAggregateType(AggregateType t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"egg\" peripheries=\"2\"]");
				for ( TagValue val : t.getItemType().values() ) {
					String sValue = sTypeName+"_"+sanitizeId( val.getName() );
					nodes.add( sValue + "[label=\""+val.getName()+"\" shape=\"box\"]");
					edges.add( sTypeName + " -> " + sValue );
				}
				return null;
			}

			@Override
			public Void visitCompoundType(CompoundType t) {
				String sTypeName = sanitizeId(t.getName());
				nodes.add( sTypeName + "[label=\""+t.getName()+"\" shape=\"octagon\" peripheries=\"2\"]");
				for ( TagType val : t.getFieldTypes() ) {
					edges.add( sTypeName + " -> " + sanitizeId(val.getName()) );
					val.accept(this);
				}
				return null;
			}

			@Override
			public Void visitTodoType(ToDoType t) {
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
