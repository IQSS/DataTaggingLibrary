package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;

/**
 * Interface for flow chart optimizers.
 * 
 * @author michael
 */
public interface FlowChartOptimizer {
   
    String getTitle();
    
    FlowChartSet optimize( FlowChartSet fcs );
    
}
