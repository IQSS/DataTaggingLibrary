package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;

/**
 * Interface for flow chart optimizers.
 * 
 * @author michael
 */
public interface FlowChartOptimizer {
   
    String getTitle();
    
    DecisionGraph optimize( DecisionGraph fcs );
    
}
