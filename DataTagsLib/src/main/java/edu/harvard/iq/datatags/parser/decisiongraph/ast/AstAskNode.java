package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AstAskNode extends AstNode {
    
    private final AstTextSubNode text;
    private final List<AstTermSubNode> terms;
    private final List<AstAnswerSubNode> answers;

    public AstAskNode(String id, AstTextSubNode aTextNode, List<AstTermSubNode> someTerms, List<AstAnswerSubNode> someAnswers ) {
        super( id );
        text = aTextNode;
        terms = someTerms;
        answers = someAnswers;
    }

    public List<AstAnswerSubNode> getAnswers() {
        return answers;
    }

    public List<AstTermSubNode> getTerms() {
        return terms;
    }

    public AstTextSubNode getTextNode() {
        return text;
    }
    
	@Override
	public <T> T accept( Visitor<T> v ) {
		return v.visit(this);
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
        if ( !(obj instanceof AstAskNode) ) {
            return false;
        }
        final AstAskNode other = (AstAskNode) obj;
        
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.terms, other.terms)) {
            return false;
        }
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        return equalsAsAstNode(other);
    }
     @Override
     public String toStringExtras() {
         return String.format("text:«%s» terms:%s answers:%s", getTextNode().getText(), getTerms(), getAnswers());
     }
    
}
