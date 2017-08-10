package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * A node that calls another node in a new execution frame.
 * @author michael
 */
public class CallNode extends ThroughNode {
	
	private Node calleeNode;
    
	public CallNode(String id) {
		super(id);
	}

	public CallNode(String id, Node calleeNode ) {
		super(id);
		this.calleeNode = calleeNode;
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visit(this);
	}
	
	public Node getCalleeNode() {
		return calleeNode;
	}

    public void setCalleeNode(Node calleeNode) {
        this.calleeNode = calleeNode;
    }

    @Override
    public int hashCode() {
        return getId().hashCode();
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
        final CallNode other = (CallNode) obj;
        if (!Objects.equals(getId(), other.getId())) {
            return false;
        }
        if (!(calleeNode != null ? calleeNode.getId().equals(other.calleeNode != null ? other.calleeNode.getId() : true) : true)){
            return false;
        }
        if (!(getNextNode() != null ? getNextNode().getId().equals(other.getNextNode() != null ? other.getNextNode().getId() : true) : true)){
            return false;
        }
        return true;
    }

    

    private Integer hashCallee(Node nd){
        if (nd != null){
            nd.accept(new Node.Visitor<Integer>() {
                @Override
                public Integer visit(ConsiderNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();
                }

                @Override
                public Integer visit(AskNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();         
                }

                @Override
                public Integer visit(SetNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();
                }

                @Override
                public Integer visit(SectionNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();
                }

                @Override
                public Integer visit(RejectNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();  
                }

                @Override
                public Integer visit(CallNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode(); 
                }

                @Override
                public Integer visit(ToDoNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();
                }

                @Override
                public Integer visit(EndNode nd) throws DataTagsRuntimeException {
                    return nd.hashCode();
                }
            });
        }
        return 0;
    }

    @Override
    protected String toStringExtras() {
        return getCalleeNode() != null ? "callee:" + getCalleeNode().getId() : "callee null";
    }
	
    
}
