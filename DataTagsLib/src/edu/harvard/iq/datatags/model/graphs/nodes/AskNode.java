package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * A node where the user has to provide an answer to a question.
 *
 * @author michael
 */
public class AskNode extends Node {

    /**
     * Keeps an ordered list of the answers
     */
    private final List<Answer> answers = new LinkedList<>();
    private final Map<Answer, Node> nextNodeByAnswer = new HashMap<>();
    private final Map<String, String> terms = new HashMap<>();
    private String text;

    public AskNode(String id) {
        super(id);
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
    public <T extends Node> T setNodeFor(Answer answer, T node) {
        answers.add(answer);
        nextNodeByAnswer.put(answer, node);
        return node;
    }

    public Node getNodeFor(Answer answer) {
        return nextNodeByAnswer.get(answer);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Set<String> getTermNames() {
        return terms.keySet();
    }

    public String getTermText(String termName) {
        return terms.get(termName);
    }

    public void addTerm(String termName, String termText) {
        terms.put(termName, termText);
    }

    public List<Answer> getAnswers() {
        return answers;
    }

    @Override
    public String toString() {
        return String.format("[AskNode id:%s text:'%s']", getId(),
                (text != null)
                        ? (text.length() > 20 ? text.substring(0, 20) : text)
                        : "<null>");
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
        if (!(obj instanceof AskNode)) {
            return false;
        }
        final AskNode other = (AskNode) obj;
        if (!Objects.equals(this.text, other.text)) {
            return false;
        }
        if (!Objects.equals(this.nextNodeByAnswer, other.nextNodeByAnswer)) {
            return false;
        }
        if (!Objects.equals(this.terms, other.terms)) {
            return false;
        }
        return equalsAsNode(other);
    }

}
