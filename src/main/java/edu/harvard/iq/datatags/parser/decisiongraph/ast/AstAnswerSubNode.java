package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions.BooleanExpressionAst;
import java.util.List;
import java.util.Objects;

/**
 * 
 * @author Michael Bar-Sinai
 */
public class AstAnswerSubNode {
    private final List<? extends AstNode> subGraph;
    private final String answerText;
    private final BooleanExpressionAst boolExp;

    public AstAnswerSubNode(String text, List<? extends AstNode> implementation, BooleanExpressionAst boolExp) {
        answerText = text;
        this.subGraph = implementation;
        this.boolExp = boolExp;
    }

	public String getAnswerText() {
		return answerText;
	}
	
    public List<? extends AstNode> getSubGraph() {
        return subGraph;
    }

    public BooleanExpressionAst getBoolExp() {
        return boolExp;
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
        if ( ! (obj instanceof AstAnswerSubNode)) {
            return false;
        }
        final AstAnswerSubNode other = (AstAnswerSubNode) obj;
        if (!Objects.equals(this.answerText, other.answerText)) {
            return false;
        }
        return Objects.equals(this.subGraph, other.subGraph);
    }
    
    @Override
    public String toString() {
        return "{" + getAnswerText() + ":" + getSubGraph() + "}";
    }
    
    
    
}
