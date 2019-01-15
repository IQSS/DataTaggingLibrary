package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;

/**
 *
 */
public class AstConsiderOptionSubNode {

    private final List<? extends AstNode> subGraph;
    private List<String> optionList;
    private List<AstSetNode.Assignment> assignments;

    public AstConsiderOptionSubNode(List<?> answer, List<? extends AstNode> implementation) {
        if (answer.get(0) instanceof String) {
            optionList = (List<String>) answer;
        } else {
            assignments = (List<AstSetNode.Assignment>) answer;
        }
        this.subGraph = implementation;
    }

    public List<String> getOptionList() {
        return optionList;
    }

    public List<? extends AstNode> getSubGraph() {
        return subGraph;
    }
    public List<AstSetNode.Assignment> getAssignments(){
        return assignments;
    }
	public String getAnswerText() {
        return ( optionList != null ) ? optionList.toString()
                                      : assignments.toString();
	}
    
    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.optionList);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AstConsiderOptionSubNode)) {
            return false;
        }
        final AstConsiderOptionSubNode other = (AstConsiderOptionSubNode) obj;
       
        if (!Objects.equals(this.optionList, other.optionList)) {
            return false;
        }
        if (!Objects.equals(this.assignments, other.assignments)) {
            return false;
        }
        return Objects.equals(this.subGraph, other.subGraph);
    }

    @Override
    public String toString() {
        return "{" + getOptionList() + ":" + getSubGraph() + "}";
    }

}
