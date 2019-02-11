package edu.harvard.iq.datatags.parser.decisiongraph;

import java.util.Arrays;
import java.util.List;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Scanners;
import org.jparsec.Terminals;
import org.jparsec.Tokens;
import org.jparsec.Tokens.Tag;
import org.jparsec.pattern.Patterns;
import static org.jparsec.Scanners.notAmong;
import static org.jparsec.Scanners.string;
import org.jparsec.Tokens.Fragment;

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
        public static final Object TEXT_BODY = "text-body";
        public static final Object END_OF_LINE = "eol";
        public static final Object IMPORT_PATH = "import-path";
    }

    static final String NODE_TEXT_TERMINATORS = ":]}";
    static final List<String> NODE_STRUCTURE_TOKENS = Arrays.asList("/", "+=", "=", ",", ";",
            "[--", "[", ":", "{", "}", "]", "#", "--]",
            "else", "slot", "when", "consider", "ask", "set", "end", "reject", "call", "todo",
            "text", "terms", "answers", "options", "section", "part", "title", "#import", "as");
    static final Terminals NODE_STRUCTURE_TERMINALS = Terminals.operators(NODE_STRUCTURE_TOKENS);

    static final Parser<Object> TOKENIZER;
    
    static final Parser<Fragment> IMPORT_PATH = Patterns.notString("as").many().toScanner("path to imported file").source().map( s->Tokens.fragment(s, Tags.IMPORT_PATH));
    
    static final Parser<Void> IGNORABLES = Parsers.or(Scanners.WHITESPACES,
            Scanners.lineComment("<--"),
            Scanners.blockComment("<*", "*>"));

    static final Parser<Fragment> NODE_TEXT = notAmong(NODE_TEXT_TERMINATORS).many1()
            .source().map(s -> Tokens.fragment(s, Tags.TEXT_BODY));

    static final Parser<Fragment> KEYWORDS = Parsers.or(string("ask"), string("set"), string("end"), string("reject"), string("call"), string("todo"), string("section"))
            .source().map(s -> Tokens.fragment(s, Tags.KEYWORD));

    static final Parser<String> NODE_ID_VALUE = 
            Patterns.among(".,/~?!()@:#$%^&*_+-")
                    .or(Patterns.range('a', 'z'))
                    .or(Patterns.range('A', 'Z'))
                    .or(Patterns.range('0', '9'))
                    .many1().toScanner(Tags.NODE_ID.toString()).source();
    
    static final Parser<Fragment> NODE_ID = Parsers.between(
            Scanners.among(">"),
            NODE_ID_VALUE,
            Scanners.among("<")).map(s -> Tokens.fragment(s, Tags.NODE_ID));
    
    static {
        TOKENIZER
                = Parsers.<Object>or(
                        NODE_STRUCTURE_TERMINALS.tokenizer(),
                        NODE_ID,
                        Terminals.Identifier.TOKENIZER,
                        NODE_TEXT);
    }

    static Parser<?> nodeStructurePart(String part) {
        return NODE_STRUCTURE_TERMINALS.token(part);
    }

    static <T> Parser<T> buildParser(Parser<T> base) {
        return base.from(TOKENIZER, IGNORABLES.skipMany());
    }

}
