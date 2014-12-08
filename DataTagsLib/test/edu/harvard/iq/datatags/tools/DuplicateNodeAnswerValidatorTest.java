package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Naomi
 */
public class DuplicateNodeAnswerValidatorTest {
    
    DuplicateNodeAnswerValidator instance;
    FlowChartSetComplier fcsc;
    FlowChartSet fcs;
    FlowChartASTParser astParser;
    
    public DuplicateNodeAnswerValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new DuplicateNodeAnswerValidator();
        fcsc = new FlowChartSetComplier(new CompoundType("", ""));
        astParser = new FlowChartASTParser();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void validateDuplicateAnswerTest_noAnswers() throws BadSetInstructionException {
        String code = "(todo: there are no answers here to check)(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        List<ValidationMessage> duplicates = instance.validateDuplicateAnswers(refs);
        assertEquals(new LinkedList<String>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_noDuplicates() throws BadSetInstructionException {
        String code = "(>ask1< ask: (text: Are there any duplicates?)"
                + "(yes: (>todo1< todo: no duplicates))"
                + "(no: (>todo2< todo: still no duplicates)))"
                + "(>end1<end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        List<ValidationMessage> duplicates = instance.validateDuplicateAnswers(refs);
        assertEquals(new LinkedList<String>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_duplicates() throws BadSetInstructionException {
        String code = "(>ask1< ask: (text: Are there any duplicates?)"
                + "(yes: (>todo1< todo: there's a duplicate!))"
                + "(yes: (>todo2< todo: yes there is!))"
                + "(no: (>todo3< todo: this is just another answer)))";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        List<ValidationMessage> duplicates = instance.validateDuplicateAnswers(refs);
        // the first instruction node should be a repeat
        LinkedList<InstructionNodeRef> expected = new LinkedList<>(Arrays.asList(refs.get(0)));
        assertEquals(expected, duplicates);
    }
    
}
