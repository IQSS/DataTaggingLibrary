package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AskNodeRef extends InstructionNodeRef {
    
    private final StringBodyNodeRef text;
    private final List<TermNodeRef> terms;
    private final List<AnswerNodeRef> answers;

    public AskNodeRef(String id, StringBodyNodeRef aTextNode, List<TermNodeRef> someTerms, List<AnswerNodeRef> someAnswers ) {
        super( new TypedNodeHeadRef(id, NodeType.Ask) );
        text = aTextNode;
        terms = someTerms;
        answers = someAnswers;
    }

    

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.text);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( !(obj instanceof AskNodeRef) ) {
            return false;
        }
        final AskNodeRef other = (AskNodeRef) obj;
        
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.terms, other.terms)) {
            return false;
        }
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        return equalsAsInstructionNodeRef(other);
    }
    
    
}
