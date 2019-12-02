package edu.harvard.iq.policymodels.model.decisiongraph.nodes;

import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that logically groups part of the interview.
 * @author michael
 */
public class SectionNode extends ThroughNode implements ContainerNode {
    
    private String title;    
    private Node startNode;
    
    public SectionNode(String id, String info) {
        super(id);
        this.title = info;
    }
    
    public SectionNode(String id) {
        super(id);
    }

    public SectionNode(String info, String id, Node startNode, Node aNextNode) {
        super(id, aNextNode);
        this.title = info;
        this.startNode = startNode;
    }
    
    @Override
    public <R> R accept(Node.Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit( this );
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.title);
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
        final SectionNode other = (SectionNode) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }

        return Objects.equals(this.startNode, other.startNode);
    }
    
}
