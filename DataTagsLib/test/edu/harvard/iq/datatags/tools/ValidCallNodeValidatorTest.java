/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
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
    
    FlowChartSetComplier fcsc;
    FlowChartSet fcs;
    ValidCallNodeValidator instance;
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
    public void ValidateIdReferencesTest_noId() throws BadSetInstructionException {
        String code = "(todo: There's no id here to do anything with)(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateIdReferences(fcs);
        assertEquals(new LinkedList<String>(), messages);
    }

    @Test
    public void ValidateIdReferencesTest_validId() throws BadSetInstructionException {
        String code = "(call: ppraCompliance )" +
                      "(>ppraCompliance< ask:(text: This should work!))(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateIdReferences(fcs);
        assertEquals(new LinkedList<String>(), messages);
    }
    
    @Test
    public void ValidateIdReferencesTest_invalidId() throws BadSetInstructionException {
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
