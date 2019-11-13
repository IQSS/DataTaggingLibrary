package edu.harvard.iq.policymodels.parser.decisiongraph;

import java.util.regex.Pattern;

/**
 * A class that creates unique node ids. Uniqueness is guaranteed only 
 * for ids created by the same instance.
 * 
 * @author michael
 */
public class AstNodeIdProvider {
   
    private int nextId = 1;
    
    private final static Pattern AUTO_ID = Pattern.compile(".*\\[#[0-9]+\\]$");
    
    public static boolean isAutoId( String anId ) {
        if ( anId == null ) return false;
        
        return AUTO_ID.matcher(anId).matches();
        
    }
    
    public String nextId() {
        return "[#" + nextId++ + "]";
    }
}
