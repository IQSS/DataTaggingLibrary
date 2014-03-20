package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Parser for the chart set graphs.
 * 
 * @author michael
 */
public class FlowChartSetParser {
	
	private final Map<String, NodeRef> id2NodeRef = new HashMap<>();
	private final Map<String, NodeRef> id2Node = new HashMap<>();
	
	public FlowChartSet parse( String source, String unitName ) { 
		// TODO implement the namespace, use unit name. 
		FlowChartASTParser parser = new FlowChartASTParser();
		List<InstructionNodeRef> nodes = parser.graphParser().parse(source);
		
		// Map node refs ids to the node refs.
		initIds( nodes );
		
		
// CONT
		
		return new FlowChartSet();
	}
	
	/**
	 * Inits all the ids of the nodes in the list. Also, collects the 
	 * user-assigned ids in {@link #id2NodeRef}.
	 * @param nodeRefs 
	 */
	protected void initIds( List<InstructionNodeRef> nodeRefs ) {
		final InstructionNodeRef.Visitor<Void> visitor = new InstructionNodeRef.Visitor<Void>() {
			
			private int nextId=0;
			
			@Override
			public Void visit(AskNodeRef askRef) {
				visitSimpleNode( askRef );
				visitSimpleNode( askRef.getTextNode() );
				for ( TermNodeRef tnd:askRef.getTerms() ) {
					visitSimpleNode(tnd);
				}
				
				for ( AnswerNodeRef ans : askRef.getAnswers() ) {
					visitSimpleNode(ans);
					for ( InstructionNodeRef inr : ans.getImplementation() ) {
						inr.accept(this);
					}
				}
				
				return null;
			}

			@Override
			public Void visit(CallNodeRef callRef) {
				return visitSimpleNode(callRef);
			}

			@Override
			public Void visit(EndNodeRef nedRef) {
				return visitSimpleNode(nedRef);
			}

			@Override
			public Void visit(SetNodeRef setRef) {
				return visitSimpleNode(setRef);
			}

			@Override
			public Void visit(TodoNodeRef todoRef) {
				return visitSimpleNode(todoRef);
			}
			
			private Void visitSimpleNode( NodeRef simpleNodeRef ) {
				if ( simpleNodeRef.getId() == null ) {
					simpleNodeRef.setId(nextId());
				} else {
					id2NodeRef.put(simpleNodeRef.getId(), simpleNodeRef);
				}
				return null;
			}
			
			private String nextId() {
				return "$"+(nextId++);
			}
		};
		
		for ( InstructionNodeRef inr: nodeRefs ) {
			inr.accept(visitor);
		}
	}
}
