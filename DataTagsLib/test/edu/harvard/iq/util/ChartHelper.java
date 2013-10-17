package edu.harvard.iq.util;

import edu.harvard.iq.datatags.runtime.ChartRunningTest;
import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.runtime.DecisionNode;
import edu.harvard.iq.datatags.runtime.EndNode;
import edu.harvard.iq.datatags.runtime.FlowChart;
import edu.harvard.iq.datatags.runtime.FlowChartSet;
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
 * Helps building charts for tests.
 * 
 * @author michael
 */
public class ChartHelper {
	
	/**
	 * Generates a chart with {@code length} {@link DecisionNode}s, terminated by an {@link EndNode}.
	 * The ids of the nodes are the id of the chart + "_" + ordinal.
	 * @param id id of the chart
	 * @param length amount of decision nodes - not including the end node.
	 * @return the chart.
	 */
	public static FlowChart linearYesChart( String id, int length ) {
		FlowChart retVal = new FlowChart(id);
		
		DecisionNode last = retVal.add( new DecisionNode( id + "_1" ) );
		retVal.setStart( last );
		for ( int count=0; count<length-1; count++ ) {
			last = last.setNodeFor(Answer.YES, retVal.add(new DecisionNode(id + "_" + (count+2))));
		}
		last.setNodeFor(Answer.YES, new EndNode( id + "_END") );
		return retVal;
	}
	
	public static FlowChartSet chartSet( FlowChart... flowCharts ) {
		FlowChartSet fcs = new FlowChartSet( flowCharts[0].getId()+"_set" );
		for ( FlowChart fc : flowCharts ) {
			fcs.addChart(fc );
		}
		return fcs;
	}
	
	public static void assertExecutionTrace(FlowChartSet fcs, String flowChartName, List<String> expectedIds) {
		assertExecutionTrace(fcs, flowChartName, expectedIds, true);
	}
	
	public static void assertExecutionTrace(FlowChartSet fcs, String flowChartName, List<String> expectedIds, boolean logToStdOut) {
		Iterable<Answer> yesMan = new Iterable<Answer>(){
			@Override
			public Iterator<Answer> iterator() {
				return new Iterator<Answer>(){
						@Override
						public boolean hasNext() { return true; }

						@Override
						public Answer next() { return Answer.YES; }

						@Override
						public void remove() {}
						
			};}
		};
		
		assertExecutionTrace(fcs, flowChartName, yesMan, expectedIds, logToStdOut);
	}
	
	public static void assertExecutionTrace(FlowChartSet fcs, String flowChartName, Iterable<Answer> answers, Iterable<String> expectedIds) {
		assertExecutionTrace(fcs, flowChartName, answers, expectedIds, true);
	}
	public static void assertExecutionTrace(FlowChartSet fcs, String flowChartName, Iterable<Answer> answers, Iterable<String> expectedIds, boolean logToStdOut) {
		RuntimeEngine ngn = new RuntimeEngine();
		ngn.setChartSet(fcs);
		RuntimeEngineTracingListener l = ngn.setListener(
											new RuntimeEngineTracingListener( 
													logToStdOut ? new RuntimeEnginePrintStreamListener()
																: new RuntimeEngineSilentListener()) );
		Iterator<Answer> answerItr = answers.iterator();
		try {
			ngn.start( flowChartName );
			while ( ngn.consume(answerItr.next()) ) {}
		
		} catch ( DataTagsRuntimeException ex ) {
			Logger.getLogger(ChartRunningTest.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		assertEquals( expectedIds,
				      l.getVisitedNodeIds() );
	}
}
