package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * A reference to a node that has a String body.
 * @author Michael Bar-Sinai
 */
public class StringBodyNodeRef extends InstructionNodeRef {
    private final String body;

    public StringBodyNodeRef(TypedNodeHeadRef head, String body) {
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
        if ( ! (obj instanceof StringBodyNodeRef)) {
            return false;
        }
        final StringBodyNodeRef other = (StringBodyNodeRef) obj;
        return other.body.equals( getBody() ) && equalsAsInstructionNodeRef(other);
    }
    
    @Override
    public String toString() {
        return "[StringBodyNodeRef super:" + super.toString() + " body:" + getBody() + "]";
    }
}
