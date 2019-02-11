package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;

/**
 * Runtime exception wrapper for {@link DataTagsParseException}s. Used internally
 * in the complier to throw {@link DataTagsParseException} across methods that do
 * not have a {@code throws} clause, e.g. {@link Node.Visitor}.
 * @author michael
 */
class RuntimeDataTagsParseException extends RuntimeException {

    public RuntimeDataTagsParseException( DataTagsParseException cause ) {
        super(cause);
    }
    
    @Override
    public DataTagsParseException getCause() {
        return (DataTagsParseException) super.getCause();
    }
    
    
    
}
