package edu.harvard.iq.policymodels.model.decisiongraph.nodes;

import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that structurally groups other nodes together.
 * @author mor
 */
public class PartNode extends Node implements ContainerNode {

    private Node startNode;
    
    public PartNode( Node startNode, String anId) {
        super(anId);
        this.startNode = startNode;
    }
    
    public PartNode(String anId) {
        this( null, anId );
    }
    
    @Override
    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit( this );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.startNode);
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
        final PartNode other = (PartNode) obj;
        return Objects.equals(this.startNode, other.startNode);
    } 
    
    
    
}
