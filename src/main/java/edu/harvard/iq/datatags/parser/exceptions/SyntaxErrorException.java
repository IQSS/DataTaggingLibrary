package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;

/**
 * An exception thrown when there is an error in the syntax, and
 * an AST cannot be created.
 * @author michael
 */
public class SyntaxErrorException extends DataTagsParseException {

	public SyntaxErrorException(CompilationUnitLocationReference where, String message) {
		super(where, message);
	}

	public SyntaxErrorException(CompilationUnitLocationReference where, String message, Throwable cause) {
		super(where, message, cause);
	}

	
}
