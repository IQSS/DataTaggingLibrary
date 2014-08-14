package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.ChartEntity;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

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
    private final Set<ChartEntity> entities = new HashSet<>();
    
    
    public ValidationMessage(Level m, String message, ChartEntity... involvedEntities) {
        level = m;
        this.message = message;
        entities.addAll(Arrays.asList(involvedEntities));
    }
    
    public Level getLevel() {
        return level;
    }
    
    public String getMessage() {
        return message;
    }
    
    @Override
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
        if ( ! (obj instanceof ValidationMessage) ) {
            return false;
        }
        final ValidationMessage other = (ValidationMessage) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return modelsEqual(other);
    }

    public Set<ChartEntity> getEntities() {
        return entities;
    }
    
    /**
     * Tests equality on the models involved ({@link ChartEntitiy}s and {@link #Level}. Ignores the
     * text in the message.
     * @param other the other validation message
     * @return {@code true} iff the entities and the level are the same.
     */
    public boolean modelsEqual( ValidationMessage other ) {
        return getLevel() == other.getLevel() && getEntities().equals(other.getEntities());
    }
    
    
}
