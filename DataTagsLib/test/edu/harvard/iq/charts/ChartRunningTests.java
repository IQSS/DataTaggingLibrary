package edu.harvard.iq.charts;

import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.runtime.DecisionNode;
import edu.harvard.iq.datatags.runtime.EndNode;
import edu.harvard.iq.datatags.runtime.FlowChart;
import edu.harvard.iq.datatags.runtime.FlowChartSet;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.RuntimeEnginePrintStreamListener;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class ChartRunningTests {
	
	@Test
	public void linearChart() {
		String flowChartName = "flowChart";
		FlowChart c1 = new FlowChart( flowChartName );
		
		DecisionNode start = c1.add( new DecisionNode("1") );
		start.setNodeFor( Answer.YES, c1.add(new DecisionNode("2")) )
			 .setNodeFor( Answer.YES, c1.add(new DecisionNode("3")) )
			 .setNodeFor( Answer.YES, c1.add(new DecisionNode("4")) )
			 .setNodeFor( Answer.YES, c1.add(new EndNode("5")) );
		
		c1.setStart(start);
		
		FlowChartSet fcs = new FlowChartSet();
		fcs.addChart(c1);
		
		RuntimeEngine ngn = new RuntimeEngine();
		ngn.setChartSet(fcs);
		ngn.setListener( new RuntimeEnginePrintStreamListener() );
		
		try {
			ngn.start( flowChartName );
			while ( ngn.consume(Answer.YES) ) {}
		
		} catch (DataTagsRuntimeException ex) {
			Logger.getLogger(ChartRunningTests.class.getName()).log(Level.SEVERE, null, ex);
		}
		
		
	}
	
	
}
