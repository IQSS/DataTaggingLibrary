package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.parser.tagspace.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.codehaus.jparsec.Parser;

/**
 * The entry point for parsing Tag Space files. Parses a file and generates a {@link TagSpaceParseResult},
 * containing all the type slots defined in the compilation unit (which is normally a file).
 * 
 * 
 * @author michael
 */
public class TagSpaceParser {
   
    private final Parser<List<AbstractSlot>> parser = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.RULES );
    
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
            
        } catch ( org.codehaus.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException( new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
											pe.getMessage(),
											pe);
        }
    }
  
    
       public TagSpaceParseResult MultiParse( String dataTags,String [] allStrings ) throws SyntaxErrorException, SemanticsErrorException 
           {
        try {
            // Parse the first string (the definition string) and then parse the rest of the string and concatenate them to the first.
             TagSpaceParseResult allParsed=new TagSpaceParseResult( parser.parse(dataTags) );
             for (int i = 0;i<=allStrings.length;i++)
             {
                List <AbstractSlot> temp = parser.parse(allStrings[i]);
                // adding the new parsed list to the big list
                allParsed.getSlots().addAll(temp);
             }
             return allParsed;
        } 
        catch ( org.codehaus.jparsec.error.ParserException pe ) {
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
