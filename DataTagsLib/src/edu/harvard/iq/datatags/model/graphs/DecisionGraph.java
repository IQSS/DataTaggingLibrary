package edu.harvard.iq.datatags.model.graphs;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single decision graph that a users walks through. A compiled decision graph file.
 * 
 * @author michael
 */
public class DecisionGraph {
	
	private static final AtomicInteger INDEX = new AtomicInteger(0);
	
	private URL source;
	private Node start;
    private CompoundType topLevelType = null;
	private final Map<String, Node> nodes = new TreeMap<>();
    private String id;
	
	private final Node.Visitor<Node> nodeAdder = new Node.Visitor<Node>() {

		@Override
		public Node visit(AskNode nd) throws DataTagsRuntimeException {
			for ( Answer ans : nd.getAnswers() ) {
				if ( nd.getNodeFor(ans)!=null ) {
					nd.getNodeFor(ans).accept(this);
				}
			}
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(SetNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(CallNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(TodoNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

		@Override
		public Node visit(EndNode nd) throws DataTagsRuntimeException {
			return visitSimpleNode(nd);
		}

        @Override
        public Node visit(RejectNode nd) throws DataTagsRuntimeException {
            return visitSimpleNode(nd);
        }
		
		Node visitSimpleNode( Node n ) {
			nodes.put( n.getId(), n );
			return n;
		}
	};
    protected String title;
	
	public DecisionGraph() {
		this( "DecisionGraph-"+INDEX.incrementAndGet());
	}
	
	public DecisionGraph(String anId) {
		id = anId;
	}
    
	public URL getSource() {
		return source;
	}

	public void setSource(URL source) {
		this.source = source;
	}

	public Node getStart() {
		return start;
	}

    /**
     * Sets the start node of the chart. The passed node gets added to the chart
     * if it is not already there.
     * @param start the node from which a default chart traversal will start.
     */
    public void setStart(Node start) {
        if ( ! nodes.containsKey(start.getId()) ) {
            add( start );
        }
		this.start = start;
	}
	
	public Node getNode( String nodeId ) {
		return nodes.get(nodeId);
	}
	
	/**
	 * Adds the node, and its descendents, to the chart.
	 * @param <T> the static type of the node
	 * @param n the node
	 * @return the node, for call chaining.
	 */
	public <T extends Node> T add( T n ) {
		n.accept( nodeAdder );
		return n;
	}
    
    /**
     * Removes the passed node. Caller should validate there
     * are no nodes in the chart that reference this node.
     * @param n the node to be removed.
     */
    public void remove( Node n ) {
        nodes.remove(n.getId());
    }

	public Iterable<Node> nodes() {
		return nodes.values();
	}
    
    public Set<String> nodeIds() {
        return nodes.keySet();
    }

    public CompoundType getTopLevelType() {
        return topLevelType;
    }

    public void setTopLevelType(CompoundType topLevelType) {
        this.topLevelType = topLevelType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
