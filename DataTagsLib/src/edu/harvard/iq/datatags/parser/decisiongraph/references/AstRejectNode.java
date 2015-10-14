package edu.harvard.iq.datatags.parser.decisiongraph.references;

import java.util.Objects;

/**
 * Node reference for for {@code (reject: reason)} operation.
 * @author michael
 */
public class AstRejectNode extends AstNode {
    
    private final String reason;

    public AstRejectNode(String id, String reason) {
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
        if ( ! (obj instanceof AstRejectNode) ) {
            return false;
        }
        final AstRejectNode other = (AstRejectNode) obj;
        return Objects.equals(this.reason, other.reason) && super.equalsAsInstructionNodeRef(other);
    }
    
    
}
