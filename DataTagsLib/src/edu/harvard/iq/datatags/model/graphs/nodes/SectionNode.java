package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
<<<<<<< Updated upstream
import java.util.List;
=======
import java.util.Objects;
>>>>>>> Stashed changes

/**
 * TODO: Add
 * @author michael
 */
<<<<<<< Updated upstream
public class SectionNode extends ThroughNode {
    
    private String info;
    
    private List<Node> nodes;
=======
public class SectionNode extends ThroughNode{
    
    private String info;
    
    private Node startNode;
>>>>>>> Stashed changes
    
    public SectionNode(String info, String id) {
        super(id);
        this.info = info;
    }

<<<<<<< Updated upstream
    public SectionNode(String info, String id, Node aNextNode) {
        super(id, aNextNode);
        this.info = info;
    }
    
    
    
    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement accept
    }

    public String getInfo() {
=======
    public SectionNode(String info, String id, Node startNode, Node aNextNode) {
        super(id, aNextNode);
        this.info = info;
        this.startNode = startNode;
    }
    
    @Override
    public <R> R accept(Node.Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit( this );
    }

    public String getTitle() {
>>>>>>> Stashed changes
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
<<<<<<< Updated upstream
    
}
=======

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.info);
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
        if (!Objects.equals(this.info, other.info)) {
            return false;
        }
        if (!Objects.equals(this.startNode, other.startNode)) {
            return false;
        }
        return true;
    }
    
}
>>>>>>> Stashed changes
