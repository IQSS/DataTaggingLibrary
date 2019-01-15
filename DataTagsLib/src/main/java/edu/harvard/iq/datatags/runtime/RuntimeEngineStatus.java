package edu.harvard.iq.datatags.runtime;

/**
 * A status a {@link RuntimeEngine} may be in.
 * @author michael
 */
public enum RuntimeEngineStatus {
    /** The engine has not run yet *//** The engine has not run yet */
    Idle,
    
    /** Engine is currently processing. */ 
    Running, 
    
    /** The engine has concluded we cannot accept this dataset (sorry!).*/ 
    Reject,
    
    /** The engine is happy to accept the dataset, as long as the data tags requirements are complied with. */
    Accept,
    
    /** An error has occurred. Please ignore the status of the engine. */
    Error,
    
    /** Engine is restarting, e.g because of a user request */
    Restarting
}
