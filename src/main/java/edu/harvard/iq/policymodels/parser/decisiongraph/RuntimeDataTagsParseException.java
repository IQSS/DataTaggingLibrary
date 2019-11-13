package edu.harvard.iq.policymodels.parser.decisiongraph;

import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
import edu.harvard.iq.policymodels.parser.exceptions.DataTagsParseException;

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
