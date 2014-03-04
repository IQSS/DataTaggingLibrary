package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AnswerNodeRef extends NodeRef {
    private final StringNodeHeadRef head;
    private final List<NodeRef> implementation;

    public AnswerNodeRef(StringNodeHeadRef head, List<NodeRef> implementation) {
        this.head = head;
        this.implementation = implementation;
    }

    public StringNodeHeadRef getHead() {
        return head;
    }

    public List<NodeRef> getImplementation() {
        return implementation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.head);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AnswerNodeRef other = (AnswerNodeRef) obj;
        if (!Objects.equals(this.head, other.head)) {
            return false;
        }
        if (!Objects.equals(this.implementation, other.implementation)) {
            return false;
        }
        return true;
    }

    
    
    
}
