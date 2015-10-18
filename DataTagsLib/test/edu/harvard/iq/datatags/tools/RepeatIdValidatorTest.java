package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
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
    DecisionGraphParser dgParser;
    
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
        dgParser = new DecisionGraphParser();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateRepeatIdsTest_noId() throws DataTagsParseException {
        String code = "(call: ppraCompliance )\n" +
                      "(call: ferpaCompliance )\n" +
                      "(call: govRecsCompliance )";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(Collections.emptySet(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_diffIds() throws DataTagsParseException {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>MR2< call: ppraCompliance)";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(Collections.emptySet(), messages);
    }
    
    @Test
    public void validateRepeatIdsTest_sameIds() throws DataTagsParseException {
        String code = "(>personalData< call: medicalRecordsCompliance )" +
                      "(>medicalRecordsCompliance< call: ppraCompliance)" +
                      "(>personalData< call: ppraCompliance)";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        Set<ValidationMessage> expected = Collections.singleton(new ValidationMessage(Level.ERROR, "Duplicate node id: \"personalData\"."));
        assertEquals(expected, messages);
    }
    
    // THIS NEEDS TO FAIL: FIX THE REPEATIDVALIDATOR WITH VISITORS ASAP
    @Test
    public void validateRepeatIdsTest_layeredIds() throws DataTagsParseException {
        String code = 
                  "(>personalData< ask: (text: first )"
                + "   (yes: (>repeat< ask: "
                + "       (text: second)"
                + "       (no: (>todo1< todo: nothing!)))))"
                + "(>repeat< ask: "
                + "    (text: is this a repeat?)"
                + "    (yes: (>todo1< todo: yes.)))";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        Set<ValidationMessage> messages = instance.validateRepeatIds(refs);
        assertEquals(new HashSet<>( 
                Arrays.asList(new ValidationMessage(Level.ERROR, "Duplicate node id: \"repeat\"."),
                               new ValidationMessage(Level.ERROR, "Duplicate node id: \"todo1\"."))),
                messages);
    }

    
}
