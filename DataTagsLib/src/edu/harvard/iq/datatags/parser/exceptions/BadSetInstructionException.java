package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;

/**
 * An error thrown to indicate a {@code set} instruction that makes no sense.
 * For example, a nonexistent value or slot.
 * 
 * @author michael
 */
public class BadSetInstructionException extends DataTagsParseException {
    
    private final BadLookupException ble;
    
    public BadSetInstructionException(String msg, AstSetNode anOffendingNode) {
        this(  msg, anOffendingNode, null );
    }

    public BadSetInstructionException(String msg, AstSetNode anOffendingNode, BadLookupException aBadLookupException) {
        super( anOffendingNode, msg);
        ble = aBadLookupException;
    }

    public BadLookupException getBadLookupException() {
        return ble;
    }
    
    
}
