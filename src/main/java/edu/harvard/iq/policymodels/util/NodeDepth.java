package edu.harvard.iq.policymodels.util;

import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import java.util.Objects;

/**
 * Utility class holding a node and its depth in the graph.
 * 
 * @author michael
 * @param <N>
 */
public class NodeDepth<N extends Node> {
   public final int depth;
   public final N node;

    public NodeDepth(int depth, N node) {
        this.depth = depth;
        this.node = node;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + this.depth;
        hash = 79 * hash + Objects.hashCode(this.node);
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NodeDepth other = (NodeDepth) obj;
        if (this.depth != other.depth) {
            return false;
        }
        if (!Objects.equals(this.node, other.node)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NodeDepth{" + "depth=" + depth + ", node=" + node + '}';
    }
   
   
}
