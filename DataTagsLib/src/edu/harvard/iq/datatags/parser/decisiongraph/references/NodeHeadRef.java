package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * A node head that contains a String title (rather than a {@link NodeType} type).
 * @author Michael Bar-Sinai
 */
public class NodeHeadRef {
    private final String title;
    private final String id;

    public NodeHeadRef(String anId, String aTitle) {
        id = anId;
        title = aTitle;
    }
    
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 43 * hash + Objects.hashCode(getTitle());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( !(obj instanceof NodeHeadRef) ) {
            return false;
        }
        final NodeHeadRef other = (NodeHeadRef) obj;
        return Objects.equals(getTitle(), other.getTitle()) &&
                Objects.equals(getId(), other.getId());
    }
    
    @Override
    public String toString() {
        return "[headref id:" + getId() + " title:" + getTitle() + "]";
    }
    
}
