package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.model.graphs.ConsiderAnswer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A node where the user has to provide an answer to a question.
 *
 * @author michael
 */
public class ConsiderNode extends Node {

    private final List<ConsiderAnswer> answers = new LinkedList<>();
    private final Map<ConsiderAnswer, Node> nextNodeByAnswer = new HashMap<>();
    private final Node _elseNode;

    public ConsiderNode(String id, Node elseNode) {
        super(id);
        _elseNode = elseNode;

    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit(this);
    }

    /**
     * Sets the node for the passed answer
     *
     * @param <T> the actual type of the node
     * @param answer the answer for which the node applies
     * @param node the node
     * @return {@code node}, for convenience, call chaining, etc.
     */
    public <T extends Node> T setNodeFor(ConsiderAnswer answer, T node) {
        if ( ! answers.contains(answer) ) {
            answers.add(answer);
        }
        nextNodeByAnswer.put(answer, node);
        return node;
    }

    public Node getNodeFor(ConsiderAnswer answer) {
        return nextNodeByAnswer.get(answer);
    }


    public Node getElseNode() {
        return _elseNode;
    }

    public List<ConsiderAnswer> getAnswers() {
        return answers;
    }
    
    
    @Override
    public String toString() {
        return String.format("[ConsiderNode id:%s ]", getId());
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(getId());
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConsiderNode)) {
            return false;
        }
        final ConsiderNode other = (ConsiderNode) obj;
        
        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        if (!Objects.equals(this.nextNodeByAnswer, other.nextNodeByAnswer)) {
            return false;
        }
        if (!Objects.equals(this._elseNode, other._elseNode)) {
            return false;
        }

        return equalsAsNode(other);
    }

}
