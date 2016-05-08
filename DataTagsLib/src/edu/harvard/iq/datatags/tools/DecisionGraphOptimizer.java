package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;

/**
 * Interface for decision graphs optimizers - objects that take a decision graph
 * and return an improved version of it, for some definition of "improved".
 * 
 * @author michael
 */
public interface DecisionGraphOptimizer {
   
    String getTitle();
    
    DecisionGraph optimize( DecisionGraph fcs );
    
}
