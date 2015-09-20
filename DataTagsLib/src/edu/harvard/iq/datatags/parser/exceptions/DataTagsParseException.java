package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.definitions.ast.CompilationUnitLocationReference;

/**
 * Base class for exceptions thrown from the data tags parser.
 * @author michael
 */
public class DataTagsParseException extends Exception {
	
	private CompilationUnitLocationReference where;

	public DataTagsParseException(CompilationUnitLocationReference where, String message) {
		super(message);
		this.where = where;
	}

	public DataTagsParseException(CompilationUnitLocationReference where, String message, Throwable cause) {
		super(message, cause);
		this.where = where;
	}

	public CompilationUnitLocationReference getWhere() {
		return where;
	}
	
	
}
