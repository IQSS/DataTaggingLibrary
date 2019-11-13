package edu.harvard.iq;

import org.parboiled.BaseParser;
import org.parboiled.Parboiled;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.errors.ParseError;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.support.ParseTreeUtils;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author michael
 */
public class ParboiledGames {

    public static void main(String[] args) {
//        String input = "1+2";
//        CalculatorParser parser = Parboiled.createParser(CalculatorParser.class);

        String input = "myslot >= value";
        AtomicSlotBooleanExpressionParser parser = Parboiled.createParser(AtomicSlotBooleanExpressionParser.class);

        ParsingResult<?> result = new ReportingParseRunner(parser.ASBE()).run(input);
        System.out.println( "Match:" + result.matched );
        if ( ! result.matched ) { 
            for( ParseError pe : result.parseErrors) {
                System.out.println(pe.getErrorMessage() + ": " + pe.getStartIndex() + "-" + pe.getEndIndex());
            }
        } else {
            String parseTreePrintOut = ParseTreeUtils.printNodeTree(result);
            System.out.println(parseTreePrintOut);
        }
    }
}


@BuildParseTree
class AtomicSlotBooleanExpressionParser extends BaseParser<Object>{
    
    Rule ASBE() {
        return Sequence( Optional(ws()), SlotRef(), Optional(ws()),
                         Op(), Optional(ws()), ValueLiteral(), Optional(ws()) );
    }
    
    Rule SlotRef() {
        return OneOrMore( CharRange('a', 'z') );//, CharRange('A','Z'), AnyOf("_/") );
    }
    
    Rule Op() { 
        return FirstOf( ">=", "<=", "=", ">", "<");
    }
    
    Rule ValueLiteral() {
        return OneOrMore( CharRange('a', 'z') );//, CharRange('A','Z'), AnyOf("_") );
    }
    
    Rule ws() {
        return OneOrMore( FirstOf(" ","\t", "\n", "\r") );
    }
}


@BuildParseTree 
class CalculatorParser extends BaseParser<Object> {

    Rule Expression() {
        return Sequence(
            Term(),
            ZeroOrMore(AnyOf("+-"), Term())
        );
    }

    Rule Term() {
        return Sequence(
            Factor(),
            ZeroOrMore(AnyOf("*/"), Factor())
        );
    }

    Rule Factor() {
        return FirstOf(
            Number(),
            Sequence('(', Expression(), ')')
        );
    }

    Rule Number() {
        return OneOrMore(CharRange('0', '9'));
    }
}