package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Gives information on a problem in the questionnaire.
 * Used with Nodes (since it has ChartEntities).
 * 
 * @author Naomi
 */
public class NodeValidationMessage extends ValidationMessage {
    
    private final Set<Node> entities = new HashSet<>();

    public NodeValidationMessage(Level l, String m, Node... involvedEntities) {
        super(l, m);
        entities.addAll( Arrays.asList(involvedEntities) );
    }
    
    public Set<Node> getEntities() {
        return entities;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof NodeValidationMessage) ) {
            return false;
        }
        final NodeValidationMessage other = (NodeValidationMessage) obj;
        if (!Objects.equals(this.message, other.message)) {
            return false;
        }
        return modelsEqual(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    /**
     * Tests equality on the models involved ({@link ChartEntitiy}s and {@link #Level}. Ignores the
     * text in the message.
     * @param other the other validation message
     * @return {@code true} iff the entities and the level are the same.
     */
    public boolean modelsEqual( NodeValidationMessage other ) {
        return getLevel() == other.getLevel() && getEntities().equals(other.getEntities());
    }
    
}
