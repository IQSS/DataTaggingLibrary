package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import java.util.List;
import java.util.Objects;
import static java.util.stream.Collectors.joining;

/**
 *ast consider node
 */
public class AstConsiderNode extends AstNode {

    private final List<String> slot;
    private final List<AstConsiderOptionSubNode> options;
    private final List<? extends AstNode> elseNode;

    public AstConsiderNode(String id, List<String> slot, List<AstConsiderOptionSubNode> someOptions, List<? extends AstNode> elseNode) {
        super(id);
        this.slot = slot;
        options = someOptions;
        this.elseNode = elseNode;
    }

    public List<? extends AstNode> getElseNode() {
        return elseNode;
    }

    public List<String> getSlot() {
        return slot;
    }

    public List<AstConsiderOptionSubNode> getOptions() {
        return options;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.options);
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

        if (!Objects.equals(this.options, other.options)) {
            return false;
        }
        if (!Objects.equals(this.slot, other.slot)) {
            return false;
        }

        if (!Objects.equals(this.elseNode, other.elseNode)) {
            return false;
        }
        return equalsAsAstNode(other);
    }

    @Override
    public String toStringExtras() {
        return String.format(" options:%s", getOptions().stream().map(a->a.getOptionList()).filter(l->l!=null).map(List::toString).collect(joining(",","«","»")));
    }

}
