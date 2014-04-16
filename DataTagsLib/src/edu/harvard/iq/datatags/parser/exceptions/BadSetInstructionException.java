package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.parser.flowcharts.SetLookupResult;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;

/**
 *
 * @author michael
 */
public class BadSetInstructionException extends DataTagsParseException {
    private final SetLookupResult badResult;
    private final SetNodeRef offendingNode;
    
    public BadSetInstructionException(SetLookupResult res, SetNodeRef anOffendingNode) {
        super(null, "Bad set instruction: " + res );
        badResult = res;
        offendingNode = anOffendingNode;
    }

    public SetLookupResult getBadResult() {
        return badResult;
    }

    public SetNodeRef getOffendingNode() {
        return offendingNode;
    }
    
}
