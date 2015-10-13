package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * Node reference for for {@code (reject: reason)} operation.
 * @author michael
 */
public class RejectNodeRef extends InstructionNodeRef {
    
    private final String reason;

    public RejectNodeRef(String id, String reason) {
        super( id );
        this.reason = reason;
    }

    public String getReason() {
        return reason;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.reason);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof RejectNodeRef) ) {
            return false;
        }
        final RejectNodeRef other = (RejectNodeRef) obj;
        return Objects.equals(this.reason, other.reason) && super.equalsAsInstructionNodeRef(other);
    }
    
    
}
