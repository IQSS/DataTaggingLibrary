package edu.harvard.iq.datatags.parser.Inference;

import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst.InferencePairAst;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import java.util.Arrays;
import static java.util.Arrays.asList;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferenceRuleParserTest {
    
    public ValueInferenceRuleParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }


    /**
     * Test of valueInferrerParser method, of class ValueInferenceRuleParser.
     */
    @Test
    public void testValueInferrerParser() {
        Parser<ValueInferrerAst> sut = ValueInferenceTerminalParser.buildParser(ValueInferenceRuleParser.valueInferrerParser());
        //Atomic
        InferencePairAst firstPair = new InferencePairAst(asList(
                                        new AstSetNode.AtomicAssignment(asList("A"), "a0"), 
                                        new AstSetNode.AtomicAssignment(asList("B"), "b0")), "Blue");
        InferencePairAst secondPair = new InferencePairAst(asList(
                                        new AstSetNode.AtomicAssignment(asList("A"), "a1"), 
                                        new AstSetNode.AtomicAssignment(asList("B"), "b1")), "Red");
        ValueInferrerAst actual = sut.parse("[Color: support" 
                                            + "[A=a0; B=b0 -> Blue]"
                                            + "[A=a1; B=b1 -> Red]" 
                                            + "]");
        ValueInferrerAst expected = new ValueInferrerAst(asList("Color"), asList(firstPair, secondPair), "support");
        assertEquals(expected, actual);
        
        actual = sut.parse("[Color: comply" 
                                            + "[A=a0;      B=b0 -> Blue]"
                                            + "[A=a1; \nB=b1 -> Red] <--- encrypted\n" 
                                            + "]");
        expected = new ValueInferrerAst(asList("Color"), asList(firstPair, secondPair), "comply");
        assertEquals(expected, actual);
        
        //Aggregate
        firstPair = new InferencePairAst(asList(
                                        new AstSetNode.AggregateAssignment(asList("A"), asList("a0")),
                                        new AstSetNode.AggregateAssignment(asList("B"), asList("b0"))), "Blue");
        secondPair = new InferencePairAst(asList(
                                        new AstSetNode.AggregateAssignment(asList("A"), asList("a1")),
                                        new AstSetNode.AggregateAssignment(asList("B"), asList("b1"))), "Red");
        actual = sut.parse("[Color: support" 
                            + "[A+=a0; B+=b0 -> Blue]"
                            + "[A+=a1; B+=b1 -> Red]" 
                            + "]");
        expected = new ValueInferrerAst(asList("Color"), asList(firstPair, secondPair), "support");
        assertEquals(expected, actual);
        
        firstPair = new InferencePairAst(asList(
                                        new AstSetNode.AggregateAssignment(Arrays.asList("Base/A".split("/")), asList("a0")),
                                        new AstSetNode.AggregateAssignment(Arrays.asList("Base/B".split("/")), asList("b0"))), "Blue");
        secondPair = new InferencePairAst(asList(
                                        new AstSetNode.AggregateAssignment(Arrays.asList("Base/A".split("/")), asList("a1")),
                                        new AstSetNode.AggregateAssignment(Arrays.asList("Base/B".split("/")), asList("b1"))), "Red");
        actual = sut.parse("[Color: support" 
                            + "[Base/A+=a0; Base/B+=b0 -> Blue]"
                            + "[Base/A+=a1; Base/B+=b1 -> Red]" 
                            + "]");
        expected = new ValueInferrerAst(asList("Color"), asList(firstPair, secondPair), "support");
        assertEquals(expected, actual);
    }
    
    /**
     * Test of valueInferrersParser method, of class ValueInferenceRuleParser.
     */
    @Test
    public void testValueInferrerListParser(){
        Parser<List<ValueInferrerAst>> sut = ValueInferenceTerminalParser.buildParser(ValueInferenceRuleParser.valueInferrersParser());
        //Atomic
        InferencePairAst firstPair = new InferencePairAst(asList(
                                        new AstSetNode.AtomicAssignment(asList("A"), "a0"), 
                                        new AstSetNode.AtomicAssignment(asList("B"), "b0")), "Blue");
        InferencePairAst secondPair = new InferencePairAst(asList(
                                        new AstSetNode.AtomicAssignment(asList("A"), "a1"), 
                                        new AstSetNode.AtomicAssignment(asList("B"), "b1")), "Red");
        InferencePairAst thirdPair = new InferencePairAst(asList(
                                        new AstSetNode.AggregateAssignment(asList("TestI"), asList("yes")),
                                        new AstSetNode.AtomicAssignment(asList("TestII"), "no")), "Works");
        List<ValueInferrerAst> actual = sut.parse("[Color: support\n" 
                                                    + "[A=a0; B=b0 -> Blue]\n"
                                                    + "[A=a1; B=b1 -> Red]\n" 
                                                    + "]\n"
                                                + "[Test: comply\n"
                                                    + "[TestI+=yes; TestII=no -> Works]\n"
                                                    + "]");
        List<ValueInferrerAst> expected = asList(new ValueInferrerAst(asList("Color"), asList(firstPair, secondPair), "support"),
                                                new ValueInferrerAst(asList("Test"), asList(thirdPair), "comply"));
        assertEquals(expected, actual);
        
        
        
    }
    
}
