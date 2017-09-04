package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.ArrayList;
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
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void validateDuplicateAnswerTest_noAnswers() throws BadSetInstructionException, DataTagsParseException {
        String code = "[todo: there are no answers here to check][end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        List<? extends AstNode> refs = cu.getParsedFile().getAstNodes();
        List<ValidationMessage> duplicates = instance.validate(refs);
        assertEquals(new LinkedList<>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_noDuplicates() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>ask1< ask: {text: Are there any duplicates?}"
                + "{answers: {yes: [>todo1< todo: no duplicates]}"
                            + "{no: [>todo2< todo: still no duplicates]}}]"
                + "[>end1<end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        List<? extends AstNode> refs = cu.getParsedFile().getAstNodes();
        List<ValidationMessage> duplicates = instance.validate(refs);
        assertEquals(new LinkedList<>(), duplicates);
    } 
    
    @Test
    public void validateDuplicateAnswersTest_duplicates() throws BadSetInstructionException, DataTagsParseException {
        String code = "[>ask1< ask: {text: Are there any duplicates?}"
                + "{answers:"
                + "{yes: [>todo1< todo: there's a duplicate!]}"
                + "{yes: [>todo2< todo: yes there is!]}"
                + "{no:  [>todo3< todo: this is just another answer]}}]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        List<? extends AstNode> refs = cu.getParsedFile().getAstNodes();
        List<ValidationMessage> actual = instance.validate(refs);
        // the first instruction node should be a repeat
        List<ValidationMessage> expected = Collections.singletonList(
                new ValidationMessage(ValidationMessage.Level.WARNING,
                    "Ask node \"" + refs.get(0).getId() + "\" has duplicate answers"));
        
        assertEquals(expected, actual);
    }
    
}
