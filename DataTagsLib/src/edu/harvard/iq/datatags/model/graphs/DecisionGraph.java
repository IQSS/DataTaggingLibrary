package edu.harvard.iq.datatags.model.graphs;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.tools.ReachableNodesCollector;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A single decision graph that can be traversed (e.g for execution).
 * 
 * @author michael
 */
public class DecisionGraph {

    private static final AtomicInteger INDEX = new AtomicInteger(0);

    private URI source;
    private Node start;
    private final Map<String, Node> nodes = new HashMap<>();
    private String id;

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
     * @param aNode the node
     * @return the node, for call chaining.
     */
    public <T extends Node> T add(T aNode) {
        ReachableNodesCollector nc = new ReachableNodesCollector();
        aNode.accept(nc);
        nc.getCollectedNodes().forEach( n -> nodes.put(n.getId(), n) );
        return aNode;
    }
    
    /**
     * Collects the reachable nodes of the graph. I.e reachable from the
     * start node.
     */
    public void addAllReachableNodes() {
        ReachableNodesCollector nc = new ReachableNodesCollector();
        getStart().accept(nc);
        nc.getCollectedNodes().forEach( n -> nodes.put(n.getId(), n) );
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    /**
     * Adds {@code prefix} to all node ids in the graph.
     * @param prefix the prefix to add.
     */
    public void prefixNodeIds( String prefix ) {
        List<Node> nodeList = new ArrayList<>(nodes.values());
        if (nodeList.get(0).getId().contains(">")) return;
        nodeList.stream().forEachOrdered( n -> {
            nodes.remove(n.getId());
            n.setId( prefix + n.getId() );
            nodes.put(n.getId(), n);
        });
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + Objects.hashCode(this.id);
        hash = 83 * hash + Objects.hashCode(start);
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
        if (!Objects.equals(this.source, other.source)) {
            return false;
        }
        if (!Objects.equals(this.start, other.start)) {
            return false;
        }
        return Objects.equals(this.nodes, other.nodes);
    }

}
