package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * A reference to a node that has a String body.
 * @author Michael Bar-Sinai
 */
public class SimpleNodeRef extends InstructionNodeRef {
    private final String body;

    public SimpleNodeRef(NodeHeadRef head, String body) {
        super(head);
        this.body = body;
    }

    public String getBody() {
        return body;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 11 * hash + Objects.hashCode(this.body);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof SimpleNodeRef)) {
            return false;
        }
        final SimpleNodeRef other = (SimpleNodeRef) obj;
        return other.body.equals( getBody() ) && equalsAsInstructionNodeRef(other);
    }
    
    @Override
    public String toString() {
        return "[SimpleNodeRef super:" + super.toString() + " body:" + getBody() + "]";
    }
}
