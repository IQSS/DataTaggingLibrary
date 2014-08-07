package edu.harvard.iq.datatags.tools;

import java.util.Objects;

/**
 *
 * @author Naomi
 */
public class ValidationMessage {
    
    public enum Level {
        ERROR, WARNING, INFO
    }
    
    private final Level level;
    private final String message;
    
    
    public ValidationMessage(Level m, String message) {
        level = m;
        this.message = message;
    }
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
    
    public String toString() {
        return "Validation message: " + level + ": " + message;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ValidationMessage other = (ValidationMessage) obj;
        if (this.level != other.level) {
            return false;
        }
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return true;
    }
    
    
    
}
