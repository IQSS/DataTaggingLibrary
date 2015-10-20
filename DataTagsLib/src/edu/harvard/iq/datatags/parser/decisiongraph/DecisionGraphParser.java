package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.error.ParserException;

/**
 * Takes a string, returns a decision graph, or the AST, in case you're writing
 * some code tool.
 * 
 * @author michael
 */
public class DecisionGraphParser {
	
    
	public DecisionGraphParseResult parse( String source ) throws DataTagsParseException { 
		Parser<List<? extends AstNode>> parser = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.graphParser() );
        try {
            List<? extends AstNode> astNodeList = parser.parse(source);
            // TODO add AST-level validators here. Add results to parse result.
            return new DecisionGraphParseResult(astNodeList);
            
        } catch ( ParserException pe ) {
            throw new DataTagsParseException(new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
                    "Error parsing decision graph code: " + pe.getMessage(), pe);
        }
	}
	
}
