package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.Collections;
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
    DecisionGraphParser dgParser;
    DecisionGraph dg;
    
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
        dgParser = new DecisionGraphParser();
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void validateDuplicateAnswerTest_noAnswers() throws BadSetInstructionException, DataTagsParseException {
        String code = "[todo: there are no answers here to check][end]";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        List<ValidationMessage> duplicates = instance.validateDuplicateAnswers(refs);
        assertEquals(new LinkedList<>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_noDuplicates() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>ask1< ask: {text: Are there any duplicates?}"
                + "{answers: {yes: [>todo1< todo: no duplicates]}"
                            + "{no: [>todo2< todo: still no duplicates]}}]"
                + "[>end1<end]";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        List<ValidationMessage> duplicates = instance.validateDuplicateAnswers(refs);
        assertEquals(new LinkedList<>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_duplicates() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>ask1< ask: {text: Are there any duplicates?}"
                + "{answers:"
                + "{yes: [>todo1< todo: there's a duplicate!]}"
                + "{yes: [>todo2< todo: yes there is!]}"
                + "{no:  [>todo3< todo: this is just another answer]}}]";
        List<? extends AstNode> refs = dgParser.parse(code).getNodes();
        List<ValidationMessage> actual = instance.validateDuplicateAnswers(refs);
        // the first instruction node should be a repeat
        List<ValidationMessage> expected = Collections.singletonList(
                new ValidationMessage(ValidationMessage.Level.WARNING,
                    "Ask node \"" + refs.get(0).getId() + "\" has duplicate answers"));
        
        assertEquals(expected, actual);
    }
    
}
