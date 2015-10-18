package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;

/**
 *
 * @author michael
 */
public class BadSetInstructionException extends DataTagsParseException {
    
    // TODO - this field might need to go.
    private final TagValueLookupResult badResult;
    private final AstSetNode offendingNode;
    
    public BadSetInstructionException(String msg, AstSetNode anOffendingNode) {
        super( (AstNode)null, msg );
        offendingNode = anOffendingNode;
        badResult = null;
    }
    
    public BadSetInstructionException(TagValueLookupResult res, AstSetNode anOffendingNode) {
        super((AstNode)null, "Bad set instruction: " + res );
        badResult = res;
        offendingNode = anOffendingNode;
    }

    public TagValueLookupResult getBadResult() {
        return badResult;
    }

}
