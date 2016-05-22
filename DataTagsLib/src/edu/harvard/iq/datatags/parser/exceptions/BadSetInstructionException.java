package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;

/**
 * An error thrown to indicate a {@code set} instruction that makes no sense.
 * For example, a nonexistent value or slot.
 * 
 * @author michael
 */
public class BadSetInstructionException extends DataTagsParseException {
    
    // TODO - this field might need to go.
    private final TagValueLookupResult badResult;
    
    public BadSetInstructionException(String msg, AstSetNode anOffendingNode) {
        super( anOffendingNode, msg );
        badResult = null;
    }
    
    public BadSetInstructionException(TagValueLookupResult res, AstSetNode anOffendingNode) {
        super(anOffendingNode, "Bad set instruction: " + res );
        badResult = res;
    }

    public TagValueLookupResult getBadResult() {
        return badResult;
    }
}
