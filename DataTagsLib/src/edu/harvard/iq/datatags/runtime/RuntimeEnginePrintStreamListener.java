package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.io.PrintStream;

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
		out.println( title(ngn) +"started");
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
		return "Engine " + ngn.getId() + ": ";
	}
	
}
