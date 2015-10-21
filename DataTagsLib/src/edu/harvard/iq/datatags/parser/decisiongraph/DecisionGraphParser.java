package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static jdk.nashorn.internal.objects.NativeRegExp.source;
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
            
            return new DecisionGraphParseResult(astNodeList);
            
        } catch ( ParserException pe ) {
            throw new DataTagsParseException(new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
                    "Error parsing decision graph code: " + pe.getMessage(), pe);
        }
	}
	
    public DecisionGraphParseResult parse( Path file ) throws DataTagsParseException, IOException { 
        
        
		Parser<List<? extends AstNode>> parser = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.graphParser() );
        try {
            String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
            List<? extends AstNode> astNodeList = parser.parse(source);
            final DecisionGraphParseResult parseResult = new DecisionGraphParseResult(astNodeList);
            parseResult.setSource( file.toUri() );
            return parseResult;
            
        } catch ( ParserException pe ) {
            throw new DataTagsParseException(new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
                    "Error parsing decision graph code: " + pe.getMessage(), pe);
        }
	}
	
}
