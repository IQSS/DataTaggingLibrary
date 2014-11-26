package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChart;

/**
 * Interface for flow chart optimizers.
 * 
 * @author michael
 */
public interface FlowChartOptimizer {
   
    String getTitle();
    
    FlowChart optimize( FlowChart fcs );
    
}
