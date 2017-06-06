package edu.harvard.iq.datatags.tools.processors;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;

/**
 * Interface for decision graphs processors - objects that take a decision graph
 * and return an improved version of it, for some definition of "improved".
 * 
 * @author michael
 */
public interface DecisionGraphProcessor {
   
    String getTitle();
    
    DecisionGraph process( DecisionGraph fcs );
    
}
