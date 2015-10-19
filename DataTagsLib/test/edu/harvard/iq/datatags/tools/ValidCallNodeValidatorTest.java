package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParseResult;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
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
public class ValidCallNodeValidatorTest {
    
    ValidCallNodeValidator instance;
    DecisionGraphParser dgParser;
    DecisionGraph dg;
    DecisionGraphParseResult parseResult;
    
    public ValidCallNodeValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        dgParser = new DecisionGraphParser();
        instance = new ValidCallNodeValidator();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateIdReferencesTest_noId() throws BadSetInstructionException, DataTagsParseException {
        String code = "[todo: There's no id here to do anything with][end]";
        parseResult = dgParser.parse(code);
        dg = parseResult.compile(new CompoundType("", ""));
        List<NodeValidationMessage> messages = instance.validateIdReferences(dg);
        assertEquals(new LinkedList<>(), messages);
    }

    @Test
    public void validateIdReferencesTest_validId() throws BadSetInstructionException, DataTagsParseException {
        String code = "[call: ppraCompliance ]" +
                      "[>ppraCompliance< ask:{text: This should work!} {answers: {yes:[end]}}][end]";
        parseResult = dgParser.parse(code);
        dg = parseResult.compile(new CompoundType("", ""));
        
        List<NodeValidationMessage> messages = instance.validateIdReferences(dg);
        assertEquals(new LinkedList<>(), messages);
    }
    
    @Test
    public void validateIdReferencesTest_invalidId() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>invalid-node< call: ferpaCompliance ]" +
                      "[>ppraCompliance< ask:{text: This shouldn't work.}{answers: {yes:[end]}}]" +
                      "[end]";
        
        parseResult = dgParser.parse(code);
        dg = parseResult.compile(new CompoundType("", ""));
        List<NodeValidationMessage> actual = instance.validateIdReferences(dg);
        assertEquals(ValidationMessage.Level.ERROR, actual.get(0).getLevel());
        assertTrue( actual.get(0).getMessage().contains("invalid-node") );
    }
    
}
