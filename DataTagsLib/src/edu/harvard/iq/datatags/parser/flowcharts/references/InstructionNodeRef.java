package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class InstructionNodeRef {
    private final NodeHeadRef head;

    public InstructionNodeRef(NodeHeadRef head) {
        this.head = head;
    }

    public NodeHeadRef getHead() {
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
        final InstructionNodeRef other = (InstructionNodeRef) obj;
        return Objects.equals(this.head, other.head);
    }
}
