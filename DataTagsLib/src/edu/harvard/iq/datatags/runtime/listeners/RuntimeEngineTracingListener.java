package edu.harvard.iq.datatags.runtime.listeners;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A runtime engine listener that records the nodes the engine visited.
 * To allow additional listening (e.g. for logging into {@code stdout}), this listener is built with the decorator pattern.
 * Use an instance of {@link RuntimeEngineSilentListener} when no additional logging is needed.
 * 
 * @author michael
 */
public class RuntimeEngineTracingListener implements  RuntimeEngine.Listener {
	
	private RuntimeEngine.Listener decorated;
	private final LinkedList<Node> visitedNodes = new LinkedList<>();

	public RuntimeEngineTracingListener(RuntimeEngine.Listener decorated) {
		this.decorated = decorated;
	}

	public RuntimeEngineTracingListener() {
		this(new RuntimeEngineSilentListener());
	}
	
	public List<Node> getVisitedNodes() {
		return visitedNodes;
	}
	
	public List<String> getVisitedNodeIds() {
        return visitedNodes.stream().map( n -> n.getId() ).collect( Collectors.toList() );
	}
	
	@Override
	public void runStarted(RuntimeEngine ngn) {
		visitedNodes.clear();
		decorated.runStarted(ngn);
	}

	@Override
	public void processedNode(RuntimeEngine ngn, Node node) {
		visitedNodes.add( node );
		decorated.processedNode(ngn, node);
	}

	@Override
	public void runTerminated(RuntimeEngine ngn) {
		decorated.runTerminated(ngn);
	}
	
    @Override
    public void statusChanged(RuntimeEngine ngn) {
        decorated.statusChanged(ngn);
    }

    @Override
    public void sectionStarted(RuntimeEngine ngn, Node node) {
        decorated.sectionStarted(ngn, node);
    }

    @Override
    public void sectionEnded(RuntimeEngine ngn, Node node) {
        decorated.sectionEnded(ngn, node);
    }
}
