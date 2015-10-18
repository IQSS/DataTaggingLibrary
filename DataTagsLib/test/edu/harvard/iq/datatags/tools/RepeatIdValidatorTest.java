package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        List<AstNode> refs = astParser.graphParser().parse(code);
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(Collections.emptySet(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_diffIds() {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>MR2< call: ppraCompliance)";
        List<AstNode> refs = astParser.graphParser().parse(code);
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(Collections.emptySet(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_sameIds() {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>personalData< call: ppraCompliance)";
        List<AstNode> refs = astParser.graphParser().parse(code);
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        Set<ValidationMessage> expected = Collections.singleton(new ValidationMessage(Level.ERROR, "Duplicate node id: \"personalData\"."));
        assertEquals(expected, messages);
    }
    
    // THIS NEEDS TO FAIL: FIX THE REPEATIDVALIDATOR WITH VISITORS ASAP
    @Test
    public void validateRepeatIdsTest_layeredIds() {
        String code = 
                  "(>personalData< ask: (text: first )"
                + "   (yes: (>repeat< ask: "
                + "       (text: second)"
                + "       (no: (>todo1< todo: nothing!)))))"
                + "(>repeat< ask: "
                + "    (text: is this a repeat?)"
                + "    (yes: (>todo1< todo: yes.)))";
        List<AstNode> refs = astParser.graphParser().parse(code);
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(new HashSet<>( 
                Arrays.asList(new ValidationMessage(Level.ERROR, "Duplicate node id: \"repeat\"."),
                               new ValidationMessage(Level.ERROR, "Duplicate node id: \"todo1\"."))),
                messages);
    }

    
}
