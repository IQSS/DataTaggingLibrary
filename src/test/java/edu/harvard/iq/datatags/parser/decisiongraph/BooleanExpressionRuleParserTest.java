/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.BooleanExpression;
import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.GreaterThanExp;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions.GreaterThanExpAst;
import java.util.Arrays;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.parboiled.Parboiled;
import org.parboiled.parserunners.RecoveringParseRunner;
import org.parboiled.parserunners.ReportingParseRunner;
import org.parboiled.parserunners.TracingParseRunner;
import org.parboiled.support.ParsingResult;

/**
 *
 * @author mor
 */
public class BooleanExpressionRuleParserTest {
    
    @Test
    public void testTypeIndexBuilding() {
        BooleanExpressionRuleParser p = Parboiled.createParser(BooleanExpressionRuleParser.class);
        ParsingResult<?> result = new TracingParseRunner(p.SlotName()).run("S1/S2");
        
        assertEquals("S1/S2", result.resultValue);
    }
    
    @Test
    public void testDigit() {
        BooleanExpressionRuleParser p = Parboiled.createParser(BooleanExpressionRuleParser.class);
        ParsingResult<?> result = new TracingParseRunner(p.Digit()).run("1");
        
        assertEquals(1, result.parseTreeRoot.getValue());
    }
    
}
