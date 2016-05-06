package edu.harvard.iq.datatags.runtime.listeners;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * A listener that logs data to a print stream, such as System.out.
 * @author michael
 */
public class RuntimeEnginePrintStreamListener implements RuntimeEngine.Listener {
	
	private final PrintStream out;
	
	/**
	 * Constructs an instance that writes to {@code System.out}.
	 */
	public RuntimeEnginePrintStreamListener() {
		this( System.out );
	}
	
	public RuntimeEnginePrintStreamListener(PrintStream out) {
		this.out = out;
	}
	
	@Override
	public void runStarted(RuntimeEngine ngn) {
		out.println( title(ngn) +"started on " + ngn.getDecisionGraph().getId());
	}

	@Override
	public void processedNode(RuntimeEngine ngn, Node node) {
		out.println( title(ngn) +"entered: " + node.getId());
	}

	@Override
	public void runTerminated(RuntimeEngine ngn) {
		out.println( title(ngn) +"terminated");
	}

	private String title( RuntimeEngine ngn ) {
		return "Engine " + ngn.getId() + ": " + indent(ngn.getStack().size());
	}
	
	private String indent( int d ) {
		if ( d == 0 ) return "";
		char[] arr = new char[d];
		Arrays.fill(arr, 0, d, '>');
		return new String(arr);
	}

    @Override
    public void statusChanged(RuntimeEngine ngn) {}
}
