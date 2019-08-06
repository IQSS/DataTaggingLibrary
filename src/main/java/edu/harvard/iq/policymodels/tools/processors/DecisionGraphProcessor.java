package edu.harvard.iq.policymodels.tools.processors;

import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;

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
