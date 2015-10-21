package edu.harvard.iq.util;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.AtomicType;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.runtime.ChartRunningTest;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEnginePrintStreamListener;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineSilentListener;
import edu.harvard.iq.datatags.runtime.listeners.RuntimeEngineTracingListener;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static org.junit.Assert.assertEquals;

/**
 * Helps building decision graphs for tests.
 * 
 * @author michael
 */
public class DecisionGraphHelper {
	
	/**
	 * Generates a chart with {@code length} {@link AskNode}s, terminated by an {@link EndNode}.
	 * The ids of the nodes are the id of the chart + "_" + ordinal.
	 * @param id id of the chart
	 * @param length amount of decision nodes - not including the end node.
	 * @return the chart.
	 */
	public static DecisionGraph linearYesChart( String id, int length ) {
		DecisionGraph retVal = new DecisionGraph(id);
		
		AskNode last = retVal.add( new AskNode( id + "_1" ) );
		retVal.setStart( last );
		for ( int count=0; count<length-1; count++ ) {
			last = last.setNodeFor(Answer.YES, retVal.add(new AskNode(id + "_" + (count+2))));
		}
		last.setNodeFor(Answer.YES, new EndNode( id + "_END") );
		return retVal;
	}
	
    public static CompoundType mockTopLevelType() {
        CompoundType mock = new CompoundType("Mock", "A mock type to just have a type there.");
        AtomicType st1 = new AtomicType("st1",null);
        st1.registerValue("v1", null);
        st1.registerValue("v2", null);
        st1.registerValue("v3", null);
        st1.registerValue("v4", null);
        mock.addFieldType(st1);
        
        return mock;
    }
    
	
	public static void assertExecutionTrace(DecisionGraph dg, List<String> expectedIds) {
		assertExecutionTrace(dg, expectedIds, true);
	}
	
	public static void assertExecutionTrace(DecisionGraph dg, List<String> expectedIds, boolean logToStdOut) {
		Iterable<Answer> yesMan = () -> new Iterator<Answer>(){
            @Override
            public boolean hasNext() { return true; }
            
            @Override
            public Answer next() { return Answer.YES; }
            
            @Override
            public void remove() {}
            
        };
		
		assertExecutionTrace(dg, yesMan, expectedIds, logToStdOut);
	}
	
	public static void assertExecutionTrace(DecisionGraph dg, Iterable<Answer> answers, Iterable<String> expectedIds) {
		assertExecutionTrace(dg, answers, expectedIds, true);
	}
    
	public static void assertExecutionTrace(DecisionGraph dg, Iterable<Answer> answers, Iterable<String> expectedIds, boolean logToStdOut) {
		if ( dg.getTopLevelType() == null ) {
            dg.setTopLevelType( new CompoundType("placeholder","") );
        }
        RuntimeEngine ngn = new RuntimeEngine();
		ngn.setDecisionGraph(dg);
		RuntimeEngineTracingListener l = ngn.setListener(
											new RuntimeEngineTracingListener( 
													logToStdOut ? new RuntimeEnginePrintStreamListener()
																: new RuntimeEngineSilentListener()) );
		Iterator<Answer> answerItr = answers.iterator();
		try {
			if ( ngn.start() ) {
                while ( ngn.consume(answerItr.next()) ) {}
            }
		
		} catch ( DataTagsRuntimeException ex ) {
			Logger.getLogger(ChartRunningTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		assertEquals( expectedIds,
				      l.getVisitedNodeIds() );
	}
    
}
