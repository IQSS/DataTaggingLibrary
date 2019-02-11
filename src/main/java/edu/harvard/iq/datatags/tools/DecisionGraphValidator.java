package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import java.util.List;

/**
 *
 * @author michael
 */
public interface DecisionGraphValidator {
    
    public List<? extends ValidationMessage> validate( DecisionGraph graph );
    
}
