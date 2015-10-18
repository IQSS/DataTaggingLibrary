package edu.harvard.iq.datatags.model.graphs.nodes;

/**
 * A node that has no successor. Either terminates execution, or pops current frame.
 * @author michael
 */
public abstract class TerminalNode extends Node {
    
    public TerminalNode(String anId) {
        super(anId);
    }
    
}
