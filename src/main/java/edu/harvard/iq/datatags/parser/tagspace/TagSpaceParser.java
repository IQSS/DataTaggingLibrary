package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.parser.tagspace.ast.AbstractAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.jparsec.Parser;

/**
 * The entry point for parsing Tag Space files. Parses a file and generates a {@link TagSpaceParseResult},
 * containing all the type slots defined in the compilation unit (which is normally a file).
 * 
 * 
 * @author michael
 */
public class TagSpaceParser {
   
    private final Parser<List<? extends AbstractAstSlot>> parser = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.RULES );
    
    /**
     * Parse Tag Space code into a result that can be used to create actual types.
     * @param tagSpaceCode
     * @return the result of the parsing - to be used for AST level inspections and type construction.
     * @throws SyntaxErrorException
     * @throws SemanticsErrorException 
     */
    public TagSpaceParseResult parse( String tagSpaceCode ) throws SyntaxErrorException, SemanticsErrorException {
        
        try {
            // Parse and collect
            return new TagSpaceParseResult( parser.parse(tagSpaceCode) );
            
        } catch ( org.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException( new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
											pe.getMessage(),
											pe);
        }
    }
    
    public TagSpaceParseResult parse( Path file ) throws IOException, SyntaxErrorException, SemanticsErrorException { 
        
        String source = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
        final TagSpaceParseResult parseResult = parse(source);
        parseResult.setSource( file.toUri() );
        return parseResult;
            
	}
    
    
}
