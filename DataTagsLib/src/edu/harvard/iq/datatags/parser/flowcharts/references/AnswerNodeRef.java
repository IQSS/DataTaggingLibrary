package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AnswerNodeRef extends NodeRef {
    private final List<? extends InstructionNodeRef> implementation;
	private final String answerText;

    public AnswerNodeRef(StringNodeHeadRef head, List<? extends InstructionNodeRef> implementation) {
		super( head.getId() );
        answerText = head.getTitle();
        this.implementation = implementation;
    }

	public String getAnswerText() {
		return answerText;
	}
	
    public List<? extends InstructionNodeRef> getImplementation() {
        return implementation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.answerText);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof AnswerNodeRef)) {
            return false;
        }
        final AnswerNodeRef other = (AnswerNodeRef) obj;
        if (!Objects.equals(this.answerText, other.answerText)) {
            return false;
        }
        if (!Objects.equals(this.implementation, other.implementation)) {
            return false;
        }
        return equalsAsNodeRef(other);
    }

    
    
    
}
