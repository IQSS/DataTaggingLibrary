package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import java.util.List;

/**
 * Base class for validators of the AST tree
 * @author michael
 */
public interface DecisionGraphAstValidator {
 
    
    public List<ValidationMessage> validate( List<? extends AstNode> astNodes );

}
