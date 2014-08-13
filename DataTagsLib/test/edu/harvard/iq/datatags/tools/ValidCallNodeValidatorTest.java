package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
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
    FlowChartSetComplier fcsc;
    FlowChartSet fcs;
    FlowChartASTParser astParser;
    
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
        fcsc = new FlowChartSetComplier(new CompoundType("", ""));
        instance = new ValidCallNodeValidator();
        astParser = new FlowChartASTParser();
    }
    
    @After
    public void tearDown() {
    }

    
    @Test
    public void validateIdReferencesTest_noId() throws BadSetInstructionException {
        String code = "(todo: There's no id here to do anything with)(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateIdReferences(fcs);
        assertEquals(new LinkedList<String>(), messages);
    }

    @Test
    public void validateIdReferencesTest_validId() throws BadSetInstructionException {
        String code = "(call: ppraCompliance )" +
                      "(>ppraCompliance< ask:(text: This should work!))(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateIdReferences(fcs);
        assertEquals(new LinkedList<String>(), messages);
    }
    
    @Test
    public void validateIdReferencesTest_invalidId() throws BadSetInstructionException {
        String code = "(call: ferpaCompliance )" +
                      "(>ppraCompliance< ask:(text: This shouldn't work.))(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateIdReferences(fcs);
        LinkedList<ValidationMessage> expected = new LinkedList<>();
        expected.addLast(new ValidationMessage(Level.ERROR, "Call node \"[CallNode id:$0 title:null]\" calls nonexistent node."));
        assertEquals(expected, messages);
    }
    
}
