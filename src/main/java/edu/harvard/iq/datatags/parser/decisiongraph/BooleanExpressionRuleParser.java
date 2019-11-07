/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions.GreaterThanExpAst;
import java.util.Arrays;
import java.util.List;
import org.parboiled.Action;
import org.parboiled.BaseParser;
import org.parboiled.Context;
import org.parboiled.Rule;
import org.parboiled.annotations.BuildParseTree;
import org.parboiled.support.Var;

@BuildParseTree
public class BooleanExpressionRuleParser extends BaseParser<Object>{
    
    public Rule SlotName() {
        return Sequence(ZeroOrMore(String("S1"), '/'), String("S2"));
    }
    
    Rule Digit() {
        Var<Integer> i = new Var<Integer>();
    return Sequence(
        OneOrMore(Digit()),
        i.set(Integer.parseInt(match())),
        debug(i)
    );
    }
    
    

    boolean debug(String s) {
        System.out.println(s); // set breakpoint here if required
        return true;
    }

    boolean debug(Var<Integer> i) {
        System.out.println(i.get()); // set breakpoint here if required
        return true;
    }
    
    public Rule GreaterThan() {
        return Sequence(SlotName(), '>', String(), push(new GreaterThanExpAst((List<String>)pop(2), (String)pop())), pop());
    }
}

