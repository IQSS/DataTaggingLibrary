/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.Inference;

import static edu.harvard.iq.datatags.parser.Inference.ValueInferenceTerminalParser.valueInferrerStructurePart;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst.InferencePairAst;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import java.util.ArrayList;
import java.util.List;
import static java.util.stream.Collectors.toList;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Terminals;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferenceRuleParser {
    
    
    final static Parser<String> IDENTIFIER_WITH_KEYWORDS;
    
    static {
        List<Parser<?>> parsers = new ArrayList<>();
        parsers.addAll( ValueInferenceTerminalParser.VALUE_INFERRER_STRUCTURE_TOKENS.stream()
                        .filter( t -> t.toLowerCase().matches("^[a-z][a-z0-9]*$") )
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
            valueInferrerStructurePart("["), 
            Terminals.identifier().sepBy(valueInferrerStructurePart("/")),
            valueInferrerStructurePart(":"),
            InferencePairParser().many(),
            valueInferrerStructurePart("]"),
            (_a, slot, _b, inferencePair, _c) -> new ValueInferrerAst(slot, inferencePair) );
    }
    
    final static Parser<List<ValueInferrerAst>> valueInferrersParser () {
        Parser<ValueInferrerAst> singleValueInferrer = valueInferrerParser();
        return singleValueInferrer.many().cast();
    }
    
}
