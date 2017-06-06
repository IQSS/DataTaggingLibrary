package edu.harvard.iq.datatags.runtime.listeners;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;

/**
 * A listener that does nothing; a "null" pattern.
 * @author michael
 */
public class RuntimeEngineSilentListener implements RuntimeEngine.Listener {

	@Override
	public void runStarted(RuntimeEngine ngn) {}

	@Override
	public void processedNode(RuntimeEngine ngn, Node node) {}

	@Override
	public void runTerminated(RuntimeEngine ngn) {}

    @Override
    public void statusChanged(RuntimeEngine ngn) {}

    @Override
    public void sectionStarted(RuntimeEngine ngn, Node node) {
    }

    @Override
    public void sectionEnded(RuntimeEngine ngn, Node node) {
    }
}
