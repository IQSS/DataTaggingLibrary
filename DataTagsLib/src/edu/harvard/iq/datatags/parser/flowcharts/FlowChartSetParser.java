package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import java.util.List;

/**
 * Parser for the chart set graphs.
 * 
 * @author michael
 */
public class FlowChartSetParser {
	public FlowChartSet parse( String source, String unitName ) { 
		// TODO implement the namespace, use unit name. 
		FlowChartASTParser parser = new FlowChartASTParser();
		List<InstructionNodeRef> nodes = parser.graphParser().parse(source);
		
		// Map node refs ids to the node refs.
		
		
		return new FlowChartSet();
	}
}
