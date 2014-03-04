package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * A node head that contains a String title (rather than a {@link NodeType} type).
 * @author Michael Bar-Sinai
 */
public class StringNodeHeadRef extends AbstractNodeHeadRef {
    private final String title;

    public StringNodeHeadRef(String id, String title) {
        super(id);
        this.title = title;
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
        if ( !(obj instanceof StringNodeHeadRef) ) {
            return false;
        }
        final StringNodeHeadRef other = (StringNodeHeadRef) obj;
        return Objects.equals(getTitle(), other.getTitle()) &&
                Objects.equals(getId(), other.getId());
    }
    
    @Override
    public String toString() {
        return "[headref id:" + getId() + " title:" + getTitle() + "]";
    }
    
}
