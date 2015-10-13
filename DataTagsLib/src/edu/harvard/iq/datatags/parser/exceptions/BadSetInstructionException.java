package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.references.SetNodeRef;

/**
 *
 * @author michael
 */
public class BadSetInstructionException extends DataTagsParseException {
    private final TagValueLookupResult badResult;
    private final SetNodeRef offendingNode;
    
    public BadSetInstructionException(TagValueLookupResult res, SetNodeRef anOffendingNode) {
        super(null, "Bad set instruction: " + res );
        badResult = res;
        offendingNode = anOffendingNode;
    }

    public TagValueLookupResult getBadResult() {
        return badResult;
    }

    public SetNodeRef getOffendingNode() {
        return offendingNode;
    }
    
}
