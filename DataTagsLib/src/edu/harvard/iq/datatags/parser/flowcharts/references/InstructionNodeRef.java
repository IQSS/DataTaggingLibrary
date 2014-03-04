package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class InstructionNodeRef {
    private final TypedNodeHeadRef head;

    public InstructionNodeRef(TypedNodeHeadRef head) {
        this.head = head;
    }

    public TypedNodeHeadRef getHead() {
        return head;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.head);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof InstructionNodeRef) ) {
            return false;
        }
        return equalsAsInstructionNodeRef((InstructionNodeRef) obj);
    }
    
    public boolean equalsAsInstructionNodeRef( InstructionNodeRef other ) {
        return Objects.equals(head, other.getHead());
    }
    
    @Override
    public String toString() {
        return "[InstructionNodeRef head:" + getHead().toString() + " ]";
    }
}
