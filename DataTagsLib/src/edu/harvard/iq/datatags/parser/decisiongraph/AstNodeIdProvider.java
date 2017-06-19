package edu.harvard.iq.datatags.parser.decisiongraph;

/**
 * A class that creates unique node ids. Uniqueness is guaranteed only 
 * for ids created by the same instance.
 * 
 * @author michael
 */
public class AstNodeIdProvider {
   
    private int nextId = 1;
    
    public static boolean isAutoId( String anId ) {
        return anId!=null && ( anId.startsWith("[#") && anId.endsWith("]"));
    }
    
    public String nextId() {
        return "[#" + nextId++ + "]";
    }
}
