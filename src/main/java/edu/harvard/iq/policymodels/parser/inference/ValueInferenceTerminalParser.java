package edu.harvard.iq.policymodels.parser.inference;

import java.util.Arrays;
import java.util.List;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
//import org.jparsec.misc.Mapper;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferenceTerminalParser {
    
    static final List<String> VALUE_INFERRER_STRUCTURE_TOKENS = Arrays.asList("[", "]", "=", "+=", ",", "->", ":", "/", ";");
    static final Terminals VALUE_INFERRER_STRUCTURE_TERMINALS = Terminals.operators(VALUE_INFERRER_STRUCTURE_TOKENS);
    
    static Parser<?> valueInferrerStructurePart(String part) {
        return VALUE_INFERRER_STRUCTURE_TERMINALS.token(part).cast();
    }
    
    static final Parser<Object> TOKENIZER = Parsers.<Object>or(VALUE_INFERRER_STRUCTURE_TERMINALS.tokenizer(), Terminals.Identifier.TOKENIZER);

    
    static final Parser<Void> IGNORABLES = Parsers.or(Scanners.WHITESPACES, 
                                                      Scanners.lineComment("<--"),
                                                      Scanners.blockComment("<*", "*>"));
    
    static <T> Parser<T> buildParser(Parser<T> base ) {
        return base.from(TOKENIZER, IGNORABLES.skipMany());
    }
    
}
