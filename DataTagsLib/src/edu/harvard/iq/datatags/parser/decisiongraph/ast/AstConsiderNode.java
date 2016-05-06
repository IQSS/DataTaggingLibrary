package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;

/**
 *ast consider node
 */
public class AstConsiderNode extends AstNode {

    private final List<String> _slot;
    private final List<AstConsiderAnswerSubNode> answers;
    private final List<? extends AstNode> _elseNode;

    public AstConsiderNode(String id, List<String> slot, List<AstConsiderAnswerSubNode> someAnswers, List<? extends AstNode> elseNode) {
        super(id);
        _slot = slot;
        answers = someAnswers;
        _elseNode = elseNode;
    }

    public List<? extends AstNode> getElseNode() {
        return _elseNode;
    }

    public List<String> getSlot() {
        return _slot;
    }

    public List<AstConsiderAnswerSubNode> getAnswers() {
        return answers;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.answers);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AstConsiderNode)) {
            return false;
        }
        final AstConsiderNode other = (AstConsiderNode) obj;

        if (!Objects.equals(this.answers, other.answers)) {
            return false;
        }
        if (!Objects.equals(this._slot, other._slot)) {
            return false;
        }

        if (!Objects.equals(this._elseNode, other._elseNode)) {
            return false;
        }
        return equalsAsAstNode(other);
    }

    @Override
    public String toStringExtras() {
        return String.format(" answers:%s", getAnswers());
    }

}
