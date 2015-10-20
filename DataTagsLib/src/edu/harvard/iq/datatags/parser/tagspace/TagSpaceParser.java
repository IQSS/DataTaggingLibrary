package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.parser.tagspace.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
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
   
    private final Parser<List<? extends AbstractSlot>> parser = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.RULES );
    
    /**
     * Parse Tag Space code into a result that can be used to create actual types.
     * @param tagSpaceCode
     * @return
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

    /**
     * Lists the slots defined in the code. A lower-level method, useful for tools inspecting 
     * the code.
     * 
     * @param tagSpaceCode
     * @return List of all defined slots.
     * @throws SyntaxErrorException 
     */
    public List<? extends AbstractSlot> ParseSlots(String tagSpaceCode) throws SyntaxErrorException {
        try {
            List<? extends AbstractSlot> slots = parser.parse(tagSpaceCode);
            return slots;
        } catch ( org.codehaus.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException( new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
											pe.getMessage(),
											pe);
        }
    }
    
}
