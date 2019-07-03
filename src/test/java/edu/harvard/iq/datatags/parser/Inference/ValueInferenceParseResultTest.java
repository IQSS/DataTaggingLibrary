package edu.harvard.iq.datatags.parser.Inference;

import edu.harvard.iq.datatags.model.inference.AbstractValueInferrer;
import edu.harvard.iq.datatags.model.inference.SupportValueInferrer;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst.InferencePairAst;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AtomicSlotValuePair;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static java.util.Arrays.asList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
public class ValueInferenceParseResultTest {
    
    AtomicSlot as0, asB, asT;
    AtomicSlot asTI;
    CompoundSlot topType;
    
    public ValueInferenceParseResultTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws SyntaxErrorException, SemanticsErrorException {
        String typeDef = "Top: consists of A, Color, Test, TestI."
                        + "A: one of a0, a1."
                        + "Color: one of Blue, Red."
                        + "Test: one of Works, Not."
                        + "TestI: one of yes, no.";
        TagSpaceParseResult tagSpaceParseResult = new TagSpaceParser().parse(typeDef);
        topType = tagSpaceParseResult.buildType("Top").get();
        as0 = (AtomicSlot) topType.getSubSlot("A");
        asB = (AtomicSlot) topType.getSubSlot("Color");
        asT = (AtomicSlot) topType.getSubSlot("Test");
        asTI = (AtomicSlot) topType.getSubSlot("TestI");
        
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of buildValueInference method, of class ValueInferenceParseResult.
     */
    @Test
    public void testBuildValueInference() {
        
    }

    /**
     * Test of getValidationMessages method, of class ValueInferenceParseResult.
     * @throws edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException
     * @throws edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException
     */
    @Test
    public void testGetValidationMessages() throws SyntaxErrorException, SemanticsErrorException {
        
        CompoundValue cv0 = topType.createInstance();
        CompoundValue cv1 = topType.createInstance();
        CompoundValue cvR = topType.createInstance();
        CompoundValue cvB = topType.createInstance();
        CompoundValue cvT = topType.createInstance();
        CompoundValue cvTI = topType.createInstance();
        cv0.put(as0.valueOf("a0"));
        cv1.put(as0.valueOf("a1"));
        cvR.put(asB.valueOf("Red"));
        cvB.put(asB.valueOf("Blue"));
        cvT.put(asT.valueOf("Works"));
        cvTI.put(asTI.valueOf("yes"));
        
        InferencePairAst firstAstPair = new InferencePairAst(asList(new AtomicSlotValuePair(asList("A"), "a0")), "Blue");
        InferencePairAst secondAstPair = new InferencePairAst(asList(new AtomicSlotValuePair(asList("A"), "a1")), "Red");
        InferencePairAst thirdAstPair = new InferencePairAst(asList(new AtomicSlotValuePair(asList("TestI"), "yes")), "Works");
        List<ValueInferrerAst> inferences = asList(new ValueInferrerAst(asList("Color"), asList(firstAstPair, secondAstPair), "support"),
                                                new ValueInferrerAst(asList("Test"), asList(thirdAstPair), "support"));
        
        ValueInferenceParser parser = new ValueInferenceParser(topType);
        final Map<List<String>, List<String>> typesBySlot = parser.buildTypeIndex();
        ValueInferenceParseResult parse = new ValueInferenceParseResult(inferences, typesBySlot, topType);
        SupportValueInferrer.InferencePair firstValPair = new SupportValueInferrer.InferencePair(cv0,cvB);
        SupportValueInferrer.InferencePair secondValPair = new SupportValueInferrer.InferencePair(cv1,cvR);
        SupportValueInferrer.InferencePair thirdValPair = new SupportValueInferrer.InferencePair(cvTI, cvT);
        SupportValueInferrer first = new SupportValueInferrer();
        first.add(firstValPair);
        first.add(secondValPair);
        SupportValueInferrer second = new SupportValueInferrer();
        second.add(thirdValPair);
        Set<AbstractValueInferrer> expResult = Stream.of(first, second).collect(Collectors.toSet());
        Set<AbstractValueInferrer> result = parse.buildValueInference();
        assertEquals(expResult, result);
    }
    
}
