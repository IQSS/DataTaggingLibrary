package edu.harvard.iq.datatags.tools.queries;

/**
 *
 * @author michael
 */
public interface DecisionGraphQuery {
    
    public interface Listener {
        void started( DecisionGraphQuery dgq );
        void matchFound( DecisionGraphQuery dgq );
        void nonMatchFound( DecisionGraphQuery dgq );
        void done( DecisionGraphQuery dgq );
    }
    
    RunTrace getCurrentTrace();
    
}
