package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import java.util.Arrays;
import org.junit.Test;
import static edu.harvard.iq.datatags.model.values.Answer.*;
import static edu.harvard.iq.datatags.util.CollectionHelper.*;
import static edu.harvard.iq.util.ChartHelper.*;

/**
 * Here we test chart runs.
 * @author michael
 */
public class ChartRunningTest {
	
	@Test
	public void linearChart() {
		String flowChartName = "flowChart";
		FlowChart c1 = new FlowChart( flowChartName );
		
		AskNode start = c1.add( new AskNode("1") );
		start.setNodeFor( YES, c1.add(new AskNode("2")) )
			 .setNodeFor( YES, c1.add(new AskNode("3")) )
			 .setNodeFor( YES, c1.add(new AskNode("4")) )
			 .setNodeFor( YES, c1.add(new EndNode("END")) );
		
		c1.setStart(start);
		
		FlowChartSet fcs = new FlowChartSet();
        fcs.setTopLevelType( mockTopLevelType() );
		fcs.addChart(c1);
		
		assertExecutionTrace( fcs, flowChartName,
				Arrays.asList("1", "2", "3", "4", "END"), false);
				
	}

	@Test
	public void chartWithBranches() {
		String flowChartName = "flowChart";
		FlowChart c1 = new FlowChart( flowChartName );
        
		AskNode start = c1.add( new AskNode("1") );
		start.setNodeFor( YES, c1.add(new AskNode("2")) )
			 .setNodeFor( NO,  c1.add(new AskNode("3")) )
			 .setNodeFor( YES, c1.add(new AskNode("4")) )
			 .setNodeFor( NO,  c1.add(new EndNode("END")) );
		
		((AskNode)c1.getNode("1")).setNodeFor(NO,  c1.add( new AskNode("x")) );
		((AskNode)c1.getNode("2")).setNodeFor(YES, c1.add( new AskNode("xx")) );
		((AskNode)c1.getNode("3")).setNodeFor(NO,  c1.add( new AskNode("xxx")) );
		((AskNode)c1.getNode("4")).setNodeFor(YES, c1.add( new EndNode("xxxx")) );
		
		c1.setStart(start);
		
		FlowChartSet fcs = new FlowChartSet("Branching", mockTopLevelType());
		fcs.addChart(c1);
		
		assertExecutionTrace( fcs, flowChartName,
				C.list( YES, NO, YES, NO ),
				C.list("1", "2", "3", "4", "END"), false);
		
	}
	
	@Test
	public void chartWithCall() {
		String subchartName = "flowChart-sub";
		FlowChart subChart = new FlowChart( subchartName );
		
		AskNode start = subChart.add( new AskNode("A") );
		start.setNodeFor( Answer.YES, subChart.add(new AskNode("B")) )
			 .setNodeFor( Answer.YES, subChart.add(new EndNode("SUB_END")) );
		subChart.setStart(start);
		
		String mainChartName = "flowChart-main";
		FlowChart mainChart = new FlowChart( mainChartName );
		
		start = mainChart.add( new AskNode("1") );
		start.setNodeFor( Answer.YES, mainChart.add(new AskNode("2")) )
			 .setNodeFor( Answer.YES, mainChart.add(new CallNode("c-m")) )
			 .setNextNode(mainChart.add(new AskNode("3")) )
			 .setNodeFor( Answer.YES, mainChart.add(new EndNode("END")) );
		
		((CallNode)mainChart.getNode("c-m")).setCalleeChartId(subchartName);
		((CallNode)mainChart.getNode("c-m")).setCalleeNodeId("A");
		
		mainChart.setStart( start );
		
		FlowChartSet fcs = new FlowChartSet("single call", mockTopLevelType());
		fcs.addChart(mainChart);
		fcs.addChart(subChart);
		
		assertExecutionTrace(fcs, mainChartName, Arrays.asList("1","2","c-m",
																	"A","B", "SUB_END",
																"3", "END"), false );
	}
	
	@Test
	public void chartWithTailCall() {
		String subchartName = "flowChart-sub";
		FlowChart subChart = new FlowChart( subchartName );
		
		AskNode start = subChart.add( new AskNode("A") );
		start.setNodeFor( Answer.YES, subChart.add(new AskNode("B")) )
			 .setNodeFor( Answer.YES, subChart.add(new EndNode("SUB_END")) );
		subChart.setStart(start);
		
		String mainChartName = "flowChart-main";
		FlowChart mainChart = new FlowChart( mainChartName );
		
		start = mainChart.add( new AskNode("1") );
		start.setNodeFor( Answer.YES, mainChart.add(new AskNode("2")) )
			 .setNodeFor( Answer.YES, mainChart.add(new CallNode("c-m")) )
			 .setNextNode(mainChart.add(new EndNode("END")) );
		
		((CallNode)mainChart.getNode("c-m")).setCalleeChartId(subchartName);
		((CallNode)mainChart.getNode("c-m")).setCalleeNodeId("A");
		
		mainChart.setStart( start );
		
		FlowChartSet fcs = new FlowChartSet("tail call", mockTopLevelType());
		fcs.addChart(mainChart);
		fcs.addChart(subChart);
		
		assertExecutionTrace( fcs, mainChartName, C.list("1","2", "c-m","A","B","SUB_END","END"), false );
	}
	
	@Test
	public void chartWithRecursion() {
		String chartId = "rec";
		FlowChart rec = linearYesChart(chartId, 3);
		
		AskNode n2 = (AskNode)rec.getNode(chartId+"_2");
		CallNode caller = n2.setNodeFor(NO, rec.add(new CallNode("Caller")));
		caller.setCalleeChartId(chartId);
		caller.setCalleeNodeId( chartId + "_1");
		caller.setNextNode( new EndNode("CallerEnd") );
		
		FlowChartSet fcs = chartSet( rec );
		
		assertExecutionTrace( fcs, chartId,
				C.list(YES, NO, 
						YES, NO,
						YES, YES, YES ),
				C.list("rec_1", "rec_2", "Caller",
						"rec_1", "rec_2", "Caller", 
						"rec_1", "rec_2", "rec_3", "rec_END",
						"CallerEnd", "CallerEnd"),
				true );
		
	}
	
	/**
	 * Same as above, but with more stack frames.
	 */
	@Test
	public void chartWithDeeperRecursion() {
		String chartId = "rec";
		FlowChart rec = linearYesChart(chartId, 3);
		
		AskNode n2 = (AskNode)rec.getNode(chartId+"_2");
		CallNode caller = n2.setNodeFor(NO, rec.add(new CallNode("Caller")));
		caller.setCalleeChartId(chartId);
		caller.setCalleeNodeId( chartId + "_1");
		caller.setNextNode( new EndNode("CallerEnd") );
		
		FlowChartSet fcs = chartSet( rec );
		
		assertExecutionTrace( fcs, chartId,
				C.list(YES, NO, 
						YES, NO,
						YES, NO,
						YES, NO,
						YES, NO,
						YES, YES, YES ),
				C.list("rec_1", "rec_2", "Caller",
						"rec_1", "rec_2", "Caller",
						"rec_1", "rec_2", "Caller", 
						"rec_1", "rec_2", "Caller", 
						"rec_1", "rec_2", "Caller", 
						"rec_1", "rec_2", "rec_3", "rec_END",
						"CallerEnd","CallerEnd","CallerEnd","CallerEnd","CallerEnd"),
				true );
		
	}
	
	/**
	 * Testing a chart where the main chart consists only from
	 * calls to sub-charts.
	 */
	@Test
	public void testThreadedCode() {
		FlowChart main = new FlowChart("threaded-main");
		
		FlowChart subA = linearYesChart("sub_a", 3);
		FlowChart subB = linearYesChart("sub_b", 3);
		FlowChart subC = linearYesChart("sub_c", 3);
		
		CallNode start = main.add( new CallNode("1", "sub_a", "sub_a_1") );
		start.setNextNode( main.add( new CallNode("2", "sub_b", "sub_b_1")) )
			 .setNextNode( main.add( new CallNode("3", "sub_c", "sub_c_1")) )
			 .setNextNode( main.add( new EndNode("END")) );
		
		main.setStart(start);
		
		assertExecutionTrace( chartSet(main, subA, subB, subC), "threaded-main",
				C.list("1","sub_a_1", "sub_a_2","sub_a_3", "sub_a_END",
						"2","sub_b_1", "sub_b_2","sub_b_3","sub_b_END",
						"3","sub_c_1", "sub_c_2","sub_c_3","sub_c_END", 
						"END"),
				false);
	}
	
}
