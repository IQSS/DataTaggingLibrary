package edu.harvard.iq.policymodels.tools;

import edu.harvard.iq.policymodels.model.decisiongraph.DecisionGraph;
import java.util.List;

/**
 *
 * @author michael
 */
public interface DecisionGraphValidator {
    
    public List<? extends ValidationMessage> validate( DecisionGraph graph );
    
}
