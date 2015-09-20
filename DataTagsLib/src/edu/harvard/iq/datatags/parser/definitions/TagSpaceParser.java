package edu.harvard.iq.datatags.parser.definitions;

import edu.harvard.iq.datatags.parser.definitions.ast.AbstractSlot;
import edu.harvard.iq.datatags.parser.definitions.references.CompilationUnitLocationReference;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
    
    
    public TagSpaceParseResult parse( String tagSpaceCode ) throws SyntaxErrorException, SemanticsErrorException {
        
        try {
            // Parse and collect
            List<? extends AbstractSlot> slots = parser.parse(tagSpaceCode);
            Map<String, List<AbstractSlot>> slotMap = slots.stream().collect(Collectors.groupingBy(AbstractSlot::getName));
            
            // Validate that the there are no duplicate slot names
            Set<String> duplicates = slotMap.values().stream()
                    .filter( l -> l.size()>1 )
                    .map( l -> l.get(0).getName() )
                    .collect(Collectors.toSet());
            
            if ( duplicates.isEmpty() ) {
                Map<String, AbstractSlot> namedSlots = new HashMap<>();
                slotMap.values().stream()
                        .map( l -> l.get(0) )
                        .forEach( s -> namedSlots.put( s.getName(), s) );
                return new TagSpaceParseResult( namedSlots );
            } else {
                throw new SemanticsErrorException( new CompilationUnitLocationReference(-1, -1), 
                    "The following slots were defined more than once: " + duplicates );
            }
            
        } catch ( org.codehaus.jparsec.error.ParserException pe ) {
			throw new SyntaxErrorException( new CompilationUnitLocationReference(pe.getLocation().line, pe.getLocation().column),
											pe.getMessage(),
											pe);
        }
    }
    
}
