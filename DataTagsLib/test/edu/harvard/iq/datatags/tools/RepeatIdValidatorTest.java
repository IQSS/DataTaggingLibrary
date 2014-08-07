package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
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
public class RepeatIdValidatorTest {
    
    RepeatIdValidator instance;
    FlowChartASTParser astParser;
    
    public RepeatIdValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new RepeatIdValidator();
        astParser = new FlowChartASTParser();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateRepeatIdsTest_noId() {
        String code = "(call: ppraCompliance )\n" +
                      "(call: ferpaCompliance )\n" +
                      "(call: govRecsCompliance )";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        LinkedList<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(new LinkedList<String>(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_diffIds() {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>MR2< call: ppraCompliance)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        LinkedList<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(new LinkedList<String>(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_sameIds() {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>personalData< call: ppraCompliance)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        LinkedList<ValidationMessage> messages = instance.validateRepeatIds(refs);
        LinkedList<ValidationMessage> expected = new LinkedList<ValidationMessage>(Arrays.asList(new ValidationMessage(Level.ERROR, "Duplicate node id: \"personalData\".")));
       // expected.add("Validation message: ERROR: Duplicate node id: \"personalData\".");
        assertEquals(expected, messages);
    }

    
}
