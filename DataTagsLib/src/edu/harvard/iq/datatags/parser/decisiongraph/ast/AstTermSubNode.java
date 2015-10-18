package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.Objects;

/**
 * A reference to a term node - a node that explains a single term. 
 * These nodes are not as structured: Both their heads and their body are just plain text.
 * @author Michael Bar-Sinai
 */
public class AstTermSubNode {
    private final String term;
    private final String explanation;

    public AstTermSubNode(String term, String explanation) {
        this.term = term;
        this.explanation = explanation;
    }

    public String getTerm() {
        return term;
    }

    public String getExplanation() {
        return explanation;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.term);
        hash = 89 * hash + Objects.hashCode(this.explanation);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( !(obj instanceof AstTermSubNode) ) {
            return false;
        }
        final AstTermSubNode other = (AstTermSubNode) obj;
        if (!Objects.equals(this.term, other.term)) {
            return false;
        }
        return Objects.equals(this.explanation, other.explanation);
    }
    
    @Override
    public String toString() {
        return String.format("[term:«%s» explanation:«%s»]", getTerm(), getExplanation() );
    }
    
    
}
