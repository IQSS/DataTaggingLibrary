package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.List;

/**
 * TODO: Add
 * @author michael
 */
public class SectionNode extends ThroughNode {
    
    private String info;
    
    private List<Node> nodes;
    
    public SectionNode(String info, String id) {
        super(id);
        this.info = info;
    }

    public SectionNode(String info, String id, Node aNextNode) {
        super(id, aNextNode);
        this.info = info;
    }
    
    
    
    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        throw new UnsupportedOperationException("Not supported yet."); // TODO: Implement accept
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }
    
}
