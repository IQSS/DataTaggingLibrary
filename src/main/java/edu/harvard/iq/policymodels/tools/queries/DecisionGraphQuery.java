package edu.harvard.iq.policymodels.tools.queries;

/**
 *
 * @author michael
 */
public interface DecisionGraphQuery {
    
    interface Listener {
        void started( DecisionGraphQuery dgq );
        void matchFound( DecisionGraphQuery dgq );
        void nonMatchFound( DecisionGraphQuery dgq );
        void rejectionFound( DecisionGraphQuery dgq );
        void done( DecisionGraphQuery dgq );
        void loopDetected ( DecisionGraphQuery dgq );
    }
    
    RunTrace getCurrentTrace();
    
}
