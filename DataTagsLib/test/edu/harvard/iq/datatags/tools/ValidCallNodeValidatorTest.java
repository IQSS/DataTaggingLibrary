package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.io.IOException;
import java.util.ArrayList;
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
    DecisionGraph dg;
    DecisionGraphCompiler parseResult;
    
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
        instance = new ValidCallNodeValidator();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateIdReferencesTest_noId() throws BadSetInstructionException, DataTagsParseException {
        String code = "[todo: There's no id here to do anything with][end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        dg = cu.getDecisionGraph();
        List<NodeValidationMessage> messages = instance.validate(dg);
        assertEquals(new LinkedList<>(), messages);
    }

    @Test
    public void validateIdReferencesTest_validId() throws BadSetInstructionException, DataTagsParseException, IOException {
        String code = "[call: ppraCompliance ]" +
                      "[>ppraCompliance< ask:{text: This should work!} {answers: {yes:[end]}}][end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        dg = cu.getDecisionGraph();
        DecisionGraphCompiler dgc = new DecisionGraphCompiler();
        dgc.put("main path",cu);
        dgc.linkage();
        List<NodeValidationMessage> messages = instance.validate(dg);
        assertEquals(new LinkedList<>(), messages);
    }
    
    @Test
    public void validateIdReferencesTest_invalidId() throws BadSetInstructionException, DataTagsParseException, IOException {
        String code = "[>invalid-node< call: ferpaCompliance ]" +
                      "[>ppraCompliance< ask:{text: This shouldn't work.}{answers: {yes:[end]}}]" +
                      "[end]";
        CompilationUnit cu = new CompilationUnit(code);
        cu.compile(new CompoundSlot("", ""), new EndNode("[SYN-END]"), new ArrayList<>());
        dg = cu.getDecisionGraph();
        DecisionGraphCompiler dgc = new DecisionGraphCompiler();
        dgc.put("main path",cu);
        dgc.linkage();
        List<NodeValidationMessage> actual = instance.validate(dg);
        assertEquals(ValidationMessage.Level.ERROR, actual.get(0).getLevel());
        assertTrue( actual.get(0).getMessage().contains("invalid-node") );
    }
    
}
