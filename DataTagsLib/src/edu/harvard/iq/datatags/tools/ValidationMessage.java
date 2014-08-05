package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.nodes.Node;

/**
 *
 * @author Naomi
 */
public class ValidationMessage {
    
    public enum Level {
        ERROR, WARNING, INFO
    }
    
    private final Level level;
    private final Node invalidNode;
    private final String message;
    
    
    public ValidationMessage(Level m, Node invalid, String message) {
        level = m;
        invalidNode = invalid;
        this.message = message;
    }
    
    public Node getInvalidNode() {
        return invalidNode;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return "Validation message: " + level + ": " + invalidNode + ": " + message;
    }
}
