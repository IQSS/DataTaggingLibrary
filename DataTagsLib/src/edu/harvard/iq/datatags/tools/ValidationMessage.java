package edu.harvard.iq.datatags.tools;

import java.util.Objects;

/**
 * Gives information on a problem in the questionnaire.
 * Used with NodeRefs and TagValues.
 * 
 * @author Naomi
 */
public class ValidationMessage {
    
    public enum Level {
        ERROR, WARNING, INFO
    }
    
    private final Level level;
    protected final String message;
    
    
    public ValidationMessage(Level l, String m) {
        level = l;
        message = m;
}
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
    public String toString() {
        return "Validation message: " + level + ", " + message;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.level);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof ValidationMessage) ) {
            return false;
        }
        final ValidationMessage other = (ValidationMessage) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return getLevel() == other.getLevel();
    }
    
    
}
