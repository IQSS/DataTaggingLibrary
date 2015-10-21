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
import java.net.URI;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single decision graph that a users walks through. A compiled decision graph
 * file.
 *
 * @author michael
 */
public class DecisionGraph {

    private static final AtomicInteger INDEX = new AtomicInteger(0);

    private URI source;
    private Node start;
    private CompoundType topLevelType = null;
    private final Map<String, Node> nodes = new HashMap<>();
    private String id;

    protected String title;

    public DecisionGraph() {
        this("DecisionGraph-" + INDEX.incrementAndGet());
    }

    public DecisionGraph(String anId) {
        id = anId;
    }

    public URI getSource() {
        return source;
    }

    public void setSource(URI source) {
        this.source = source;
    }

    public Node getStart() {
        return start;
    }

    /**
     * Sets the start node of the chart. The passed node gets added to the chart
     * if it is not already there.
     *
     * @param start the node from which a default chart traversal will start.
     */
    public void setStart(Node start) {
        if (!nodes.containsKey(start.getId())) {
            add(start);
        }
        this.start = start;
    }

    public Node getNode(String nodeId) {
        return nodes.get(nodeId);
    }

    /**
     * Adds the node, and its descendents, to the chart.
     *
     * @param <T> the static type of the node
     * @param n the node
     * @return the node, for call chaining.
     */
    public <T extends Node> T add(T n) {
        n.accept(new Node.VoidVisitor() {

            @Override
            public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
                for (Answer ans : nd.getAnswers()) {
                    if (nd.getNodeFor(ans) != null) {
                        nd.getNodeFor(ans).accept(this);
                    }
                }
            }

            @Override
            public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
                if ( nd.hasNextNode() ) {
                    nd.getNextNode().accept(this);
                }
            }

            @Override
            public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
                if ( nd.hasNextNode() ) {
                    nd.getNextNode().accept(this);
                }
            }

            @Override
            public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
                if ( nd.hasNextNode() ) {
                    nd.getNextNode().accept(this);
                }
            }

            @Override
            public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
            }

            @Override
            public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                nodes.put(nd.getId(), nd);
            }
        });
        return n;
    }

    /**
     * Removes the passed node. Caller should validate there are no nodes in the
     * chart that reference this node.
     *
     * @param n the node to be removed.
     */
    public void remove(Node n) {
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

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(this.title);
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
        if ( ! (obj instanceof DecisionGraph) ) {
            return false;
        }
        final DecisionGraph other = (DecisionGraph) obj;
        if (!Objects.equals(this.id, other.id)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        if (!Objects.equals(this.topLevelType, other.topLevelType)) {
            return false;
        }
        return Objects.equals(this.nodes, other.nodes);
    }

}
