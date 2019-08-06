package edu.harvard.iq.policymodels.tools;

import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstNode;
import java.util.List;

/**
 * Base class for validators of the AST tree
 * @author michael
 */
public interface DecisionGraphAstValidator {
 
    
    public List<ValidationMessage> validate( List<? extends AstNode> astNodes );

}
