/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import static edu.harvard.iq.datatags.model.values.Answer.YES;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetVisualizer;
import java.nio.file.Paths;

/**
 *
 * @author michael
 */
public class GraphvizCharsetVisualizerTestRun {
	
	public static void main(String[] args) throws Exception {
		new GraphvizCharsetVisualizerTestRun().testVizualize();
	}
	
	
	/**
	 * Test of vizualize method, of class GraphivzCharsetVisualizer.
	 */
	public void testVizualize() throws Exception {
		String subchartName = "flowChart-sub";
		FlowChart subChart = new FlowChart( subchartName );
		
		AskNode start = subChart.add( new AskNode("A") );
		EndNode end = subChart.add(new EndNode("SUB_END"));
		start.setNodeFor( YES, subChart.add(new AskNode("B")) )
			 .setNodeFor( YES, end);
		subChart.setStart(start);
		subChart.connectOpenEnds(end);
		
		String mainChartName = "flowChart-main";
		FlowChart mainChart = new FlowChart( mainChartName );
		
		start = mainChart.add( new AskNode("1") );
		end = mainChart.add(new EndNode("5"));
		start.setNodeFor( YES, mainChart.add(new AskNode("2")) )
			 .setNodeFor( YES, mainChart.add(new CallNode("c-m")) )
			 .setNextNode(mainChart.add(new AskNode("3")) )
			 .setNodeFor( YES, end );
		mainChart.connectOpenEnds(end);
		
		((CallNode)mainChart.getNode("c-m")).setCalleeChartId(subchartName);
		((CallNode)mainChart.getNode("c-m")).setCalleeNodeId("A");
		mainChart.getNode("c-m").setTitle("Calling A");
		
		mainChart.setStart( start );
		
		FlowChartSet fcs = new FlowChartSet("single call");
		fcs.addChart(mainChart);
		fcs.addChart(subChart);
		
		GraphvizChartSetVisualizer v = new GraphvizChartSetVisualizer();
		
		v.setChartSet(fcs);
		v.vizualize( Paths.get("WORK/SingleCall.gv") );
		
	}
	
}
