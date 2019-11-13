package edu.harvard.iq.policymodels.parser.inference;

import static edu.harvard.iq.policymodels.parser.inference.ValueInferenceTerminalParser.valueInferrerStructurePart;
import edu.harvard.iq.policymodels.parser.inference.ast.ValueInferrerAst;
import edu.harvard.iq.policymodels.parser.inference.ast.ValueInferrerAst.InferencePairAst;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstSetNode;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import static java.util.stream.Collectors.toList;
import org.jparsec.Parser;
import org.jparsec.Parsers;
import org.jparsec.Terminals;

/**
 * 
 * @author mor_vilozni
 */
public class ValueInferenceRuleParser {
    
    
    final static Parser<String> IDENTIFIER_WITH_KEYWORDS;
    
    final static Pattern IDENTIFIER_PATTERN = Pattern.compile("^[_a-z][_a-z0-9]*$");
    
    static {
        List<Parser<?>> parsers = new ArrayList<>();
        parsers.addAll( ValueInferenceTerminalParser.VALUE_INFERRER_STRUCTURE_TOKENS.stream()
                        .filter( t -> IDENTIFIER_PATTERN.matcher(t.toLowerCase()).matches() )
                        .map( t -> valueInferrerStructurePart(t) ) 
                            .collect( toList() ) );
        IDENTIFIER_WITH_KEYWORDS =  Parsers.sequence(
                    Parsers.or(parsers).many(),
                    Terminals.identifier().many()
                ).source().map( String::trim );
    }
    
    final static Parser<AstSetNode.AtomicAssignment> ATOMIC_ASSIGNMENT_SLOT = Parsers.sequence(
            IDENTIFIER_WITH_KEYWORDS.sepBy(valueInferrerStructurePart("/")),
            valueInferrerStructurePart("="),
            IDENTIFIER_WITH_KEYWORDS,
            (path, _eq, value) -> new AstSetNode.AtomicAssignment(path, value.trim())
        );
    
    final static Parser<AstSetNode.AggregateAssignment> AGGREGATE_ASSIGNMENT_SLOT = Parsers.sequence(
            IDENTIFIER_WITH_KEYWORDS.sepBy(valueInferrerStructurePart("/")),
            valueInferrerStructurePart("+="),
            IDENTIFIER_WITH_KEYWORDS.sepBy( valueInferrerStructurePart(",") ),
            (path, _eq, value) -> new AstSetNode.AggregateAssignment(path, value)
        );
    
    final static Parser<InferencePairAst> InferencePairParser() {
        return Parsers.sequence(
                valueInferrerStructurePart("["),
                Parsers.or(ATOMIC_ASSIGNMENT_SLOT, AGGREGATE_ASSIGNMENT_SLOT).sepBy(valueInferrerStructurePart(";")),
                valueInferrerStructurePart("->"),
                IDENTIFIER_WITH_KEYWORDS,
                valueInferrerStructurePart("]"),
                (_s, assignments, _c, value, _e) -> new InferencePairAst(assignments, value));
    }
    
    /**
     * Generate a parser that will accept the sub-slot name
     */
    static Parser<ValueInferrerAst> valueInferrerParser() {
        return Parsers.sequence(
            Parsers.sequence(
                valueInferrerStructurePart("["), 
                Terminals.identifier().sepBy(valueInferrerStructurePart("/")),
                valueInferrerStructurePart(":"),
                (_open, slot, _column) -> slot 
            ),
            Terminals.identifier().optional("support"),
            InferencePairParser().many(),
            valueInferrerStructurePart("]"),
            (slot, inferrerType, inferencePair, _c) -> new ValueInferrerAst(slot, inferencePair, inferrerType) );
    }
    
    final static Parser<List<ValueInferrerAst>> valueInferrersParser () {
        Parser<ValueInferrerAst> singleValueInferrer = valueInferrerParser();
        return singleValueInferrer.many().cast();
    }
    
}
