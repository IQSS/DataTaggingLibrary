package edu.harvard.iq.policymodels.util;

/**
 * A Class that helps with strings.
 * @author michael
 */
public class StringHelper {
    
    public static boolean nonEmpty( String s ) {
        return (s != null) && !(s.trim().isEmpty());
    }
    
}
