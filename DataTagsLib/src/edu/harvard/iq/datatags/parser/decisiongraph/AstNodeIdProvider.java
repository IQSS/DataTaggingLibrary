package edu.harvard.iq.datatags.parser.decisiongraph;

/**
 * A class that creates unique node ids. Uniqueness is guaranteed only within
 * a single instance.
 * 
 * @author michael
 */
public class AstNodeIdProvider {
   
    private int nextId = 1;
    
    public String nextId() {
        return "[#" + nextId++ + "]";
    }
}
