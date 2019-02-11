package edu.harvard.iq.datatags;

/**
 * A class that returns runtime info about this PolicyModels version.
 * @author michael
 */
public final class PolicyModelsInfo {
    
    private PolicyModelsInfo() {
        // prevent instantiation.
    }
    
    public static String getVersionString() {
        return "PolicyModels 1.9.7-DEV";
    }
}
