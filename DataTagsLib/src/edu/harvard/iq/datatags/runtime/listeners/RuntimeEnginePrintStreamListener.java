package edu.harvard.iq.datatags.runtime.listeners;

import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.PrintStream;
import java.util.Arrays;

/**
 * A listener that logging data to a print stream, such as System.out.
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
		out.println( title(ngn) +"started on " + ngn.getChartSet().getId());
	}

	@Override
	public void nodeEntered(RuntimeEngine ngn, Node node) {
		out.println( title(ngn) +"entered: " + node.getId() + "(" + node.getTitle() + ")");
	}

	@Override
	public void runTerminated(RuntimeEngine ngn) {
		out.println( title(ngn) +"terminated");
	}

	@Override
	public void runError(RuntimeEngine ngn, DataTagsRuntimeException e) {
		out.println( title(ngn) +"error: " + e.getLocalizedMessage());
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
}
