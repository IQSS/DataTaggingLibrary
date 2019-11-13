package edu.harvard.iq.policymodels.parser.policyspace;

import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
import org.jparsec.Tokens;
import org.jparsec.Tokens.Fragment;
//import org.jparsec.misc.Mapper;
import org.jparsec.pattern.Patterns;

/**
 * The terminal parser (or tokenizer) for the Tag Space language.
 * 
 * @author michael
 */
public class TagSpaceTerminalParser {
   
    static final Terminals KEYWORD_TOKENS = Terminals.operators(":", "one", "some", "consists", "of", ",", ".");
    static final Parser<?> KEYWORDS = KEYWORD_TOKENS.tokenizer();
    static final Parser<?> IDENTIFIER = Terminals.Identifier.TOKENIZER;
    static final Parser<Fragment> DESCRIPTION = Scanners.nestableBlockComment("[", "]")
                                                        .source().map( s -> Tokens.fragment(s, "description"));
    static final Parser<Fragment> TODO = Patterns.regex("TODO").toScanner("todo").map( _s -> Tokens.fragment("", "todo") );
    
    static final Parser<Object> TOKENIZER = Parsers.<Object>or(KEYWORDS, TODO, IDENTIFIER, DESCRIPTION);
    static final Parser<Void> IGNORABLES = Parsers.or(Scanners.WHITESPACES, 
                                                      Scanners.lineComment("<--"),
                                                      Scanners.blockComment("<*", "*>"));
    
    
    static Parser<?> keyword(String name) {
//       return Mapper.skip(KEYWORD_TOKENS.token(name));
        return KEYWORD_TOKENS.token(name);
    }
    
    static <T> Parser<T> buildParser(Parser<T> base ) {
        return base.from(TOKENIZER, IGNORABLES.skipMany());
    }
    
}
