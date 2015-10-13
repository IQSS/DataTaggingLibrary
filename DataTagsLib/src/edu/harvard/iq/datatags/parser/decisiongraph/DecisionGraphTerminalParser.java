package edu.harvard.iq.datatags.parser.decisiongraph;

import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.Terminals;
import org.codehaus.jparsec.Tokens;
import org.codehaus.jparsec.Tokens.Tag;
import org.codehaus.jparsec.misc.Mapper;
import org.codehaus.jparsec.pattern.Patterns;
import static org.codehaus.jparsec.Scanners.notChar;
import static org.codehaus.jparsec.Scanners.string;

/**
 * Terminal parser for the decision graph language.
 * 
 * @author michael
 */
public class DecisionGraphTerminalParser {
    
    /**
     * Tags the tokenizer uses for the various tokenized parts.
     */
    public static class Tags {
        
        public static final Object NODE_STRUCTURE = Tag.RESERVED;
        public static final Object NODE_ID = "node-id";
        public static final Object KEYWORD = "keyword";
        public static final Object NODE_TEXT = "node-text";
        public static final Object SUB_NODE_TEXT = "sub-node-text";
    }
    
    
    static final Terminals NODE_STRUCTURE_TOKENS = Terminals.operators( "/", "+=","=", ",", "[", ":", "{", "}", "]","ask","set","end","reject","call","todo");
   
    static final Parser<Object> TOKENIZER;
    
    static final Parser<Void> IGNORABLES = Parsers.or(Scanners.WHITESPACES, 
                                                      Scanners.lineComment("<--"),
                                                      Scanners.blockComment("<*", "*>"));
    
    static final Parser<Tokens.Fragment> NODE_TEXT = notChar(']').many1()
                                                        .source().map(s -> Tokens.fragment(s, Tags.NODE_TEXT));
    
    
    static final Parser<Tokens.Fragment> SUB_NODE_TEXT = notChar('}').many1()
                                                        .source().map( s -> Tokens.fragment(s, Tags.SUB_NODE_TEXT));
    static final Parser<Tokens.Fragment> KEYWORDS = Parsers.or( string("ask"), string("set"), string("end"), string("reject"), string("call"), string("todo") )
                                                             .source().map( s -> Tokens.fragment(s, Tags.KEYWORD) ); 
    
    static final Parser<Tokens.Fragment> NODE_ID = Parsers.between(
            Scanners.among(">"),
            Patterns.among(".,/~?!@#$%^&*_+-").or(Patterns.range('a', 'z'))
                                .or(Patterns.range('A', 'Z')).or(Patterns.range('0','9')).many1()
                                .toScanner( Tags.NODE_ID.toString() ).source(),
            Scanners.among("<")).map( s -> Tokens.fragment(s, Tags.NODE_ID) );
    
    
    static {
        TOKENIZER = 
            Parsers.<Object>or(NODE_STRUCTURE_TOKENS.tokenizer(),
                                NODE_ID,
                                Terminals.Identifier.TOKENIZER,
                                NODE_TEXT,
                                SUB_NODE_TEXT);
    }
    
    
    static Parser<?> nodeStructurePart(String part) {
       return Mapper.skip(NODE_STRUCTURE_TOKENS.token(part));
    }
    
    static <T> Parser<T> buildParser(Parser<T> base ) {
        return base.from(TOKENIZER, IGNORABLES.skipMany());
    }
    
    static String join( List<String> list ) {
        StringBuilder sb = new StringBuilder();
        list.forEach( s -> sb.append(s).append(" ") );
        return sb.toString().trim(); 
    }
}
