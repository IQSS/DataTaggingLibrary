package edu.harvard.iq.datatags.parser.flowcharts.references;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AskNodeRef extends InstructionNodeRef {
    
    private final String text;
    private final Map<String, String> terms = new HashMap<>();
    private final Map<String, List<NodeRef>> answers = new HashMap<>();

    public AskNodeRef(TypedNodeHeadRef head, String text, List<TermNodeRef> termNodes ) {
        super( head );
        this.text = text;
        for ( TermNodeRef tnr : termNodes ) {
            terms.put( tnr.getTerm(), tnr.getExplanation() );
        }
    }

    public String getText() {
        return text;
    }
    
    public Map<String, String> getTerms() {
        return terms;
    }

    public Map<String, List<NodeRef>> getAnswers() {
        return answers;
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
        return equalsAsInstructionNodeRef(other);
    }
    
    
}
