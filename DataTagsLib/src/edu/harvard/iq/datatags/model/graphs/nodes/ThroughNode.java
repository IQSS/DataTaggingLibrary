package edu.harvard.iq.datatags.model.graphs.nodes;

/**
 * A node through which the runtime passes, with only a
 * single possible successor node. 
 * 
 * @author michael
 */
public abstract class ThroughNode extends Node {
	
	private Node nextNode;

	public ThroughNode( String id ) {
		this( id, null );
	}
	
	public ThroughNode( String id, Node aNextNode) {
		super(id);
		nextNode = aNextNode;
	}

	public Node getNextNode() {
		return nextNode;
	}

	public <T extends Node> T setNextNode(T aNode) {
		this.nextNode = aNode;
		return aNode;
	}
    
    public boolean hasNextNode() {
        return getNextNode() != null;
    }
	
}
