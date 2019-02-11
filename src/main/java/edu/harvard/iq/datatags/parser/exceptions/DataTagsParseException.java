package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;

/**
 * Base class for exceptions thrown from the data tags parser.
 * @author michael
 */
public class DataTagsParseException extends Exception {
	
	private CompilationUnitLocationReference where;
    private AstNode offendingNode;

	public DataTagsParseException(AstNode offending, String message) {
        super(message);
        offendingNode = offending;
    }
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

    public AstNode getOffendingNode() {
        return offendingNode;
    }

    public void setOffendingNode(AstNode offendingNode) {
        this.offendingNode = offendingNode;
    }
    
}
