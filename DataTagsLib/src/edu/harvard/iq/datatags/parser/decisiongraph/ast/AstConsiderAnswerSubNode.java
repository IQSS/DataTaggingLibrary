package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class AstConsiderAnswerSubNode {

    private final List<? extends AstNode> subGraph;
    private List<String> answerList;
    private List<AstSetNode.Assignment> assignments;

    public AstConsiderAnswerSubNode(List<?> answer, List<? extends AstNode> implementation) {
        if (answer.get(0) instanceof String) {
            answerList = (List<String>) answer;
        } else {
            assignments = (List<AstSetNode.Assignment>) answer;
        }
        this.subGraph = implementation;
    }

    public List<String> getAnswerList() {
        return answerList;
    }

    public List<? extends AstNode> getSubGraph() {
        return subGraph;
    }
    public List<AstSetNode.Assignment> getAssignments(){
        return assignments;
    }
	public String getAnswerText() {
		return answerList.toString()+assignments.toString();
	}
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.answerList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AstConsiderAnswerSubNode)) {
            return false;
        }
        final AstConsiderAnswerSubNode other = (AstConsiderAnswerSubNode) obj;
       
        if (!Objects.equals(this.answerList, other.answerList)) {
            return false;
        }
        if (!Objects.equals(this.assignments, other.assignments)) {
            return false;
        }
        return Objects.equals(this.subGraph, other.subGraph);
    }

    @Override
    public String toString() {
        return "{" + getAnswerList() + ":" + getSubGraph() + "}";
    }

}
