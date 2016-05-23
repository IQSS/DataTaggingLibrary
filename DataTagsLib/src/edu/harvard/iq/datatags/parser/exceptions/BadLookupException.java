package edu.harvard.iq.datatags.parser.exceptions;

import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;

/**
 * Thrown when a value of a type are not found.
 * @author michael
 */
public class BadLookupException extends DataTagsParseException {
    
    private final TagType type;
    private final String soughtName;

    public BadLookupException(TagType type, String soughtName, AstNode offending) {
        super(offending, "Type " + type.getName() + " does not have a value/sub-slot named '" + soughtName + "'");
        this.type = type;
        this.soughtName = soughtName;
    }

    public String getSoughtName() {
        return soughtName;
    }

    public TagType getType() {
        return type;
    }
    
}
