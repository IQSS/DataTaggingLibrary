package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.ChartEntity;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Naomi
 */
public class UnreachableNodeValidatorTest {
    
    UnreachableNodeValidator instance;
    FlowChartSetComplier fcsc;
    FlowChartSet fcs;
    FlowChartASTParser astParser;
    
    public UnreachableNodeValidatorTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
        instance = new UnreachableNodeValidator();
        fcsc = new FlowChartSetComplier(new CompoundType("", ""));
        astParser = new FlowChartASTParser();
    }
    
    @After
    public void tearDown() {
    }

  
    @Test
    public void validateUnreachableNodesTest_noNodes() throws BadSetInstructionException {
        String code = "";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateUnreachableNodes(fcs);
        assertEquals(new LinkedList<>(), messages);
    }
    
    @Test
    public void validateUnreachableNodesTest_reachableNodes() throws BadSetInstructionException {
        String code = "(ask: (text: Will this work?)" +
                        "(yes: (call:shouldWork) ))(end)" +
                      "(>shouldWork< ask: (text: This should work.)" +
                      "(yes: (reject: Good, it works.))" +
                      "(no: (reject: This should have worked.)))(end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateUnreachableNodes(fcs);
        assertEquals(new LinkedList<String>(), messages);
    }
   
    @Test
    public void validateUnreachableNodesTest_minimal() throws BadSetInstructionException {
        System.out.println("\n\nMinimal");
        String code = "(>r< end)(>nr< end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        
        System.out.println("refs = " + refs);
        
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateUnreachableNodes(fcs);
        
        Set<ChartEntity> expected = Collections.<ChartEntity>singleton( new EndNode("nr") );
        Set<ChartEntity> actualEntities = new HashSet<>();
        Set<ValidationMessage.Level> actualLevels = EnumSet.noneOf(ValidationMessage.Level.class);
        
        for ( ValidationMessage vm : messages ) {
            actualEntities.addAll(vm.getEntities());
            actualLevels.add(vm.getLevel());
        }
        System.out.println("actual = " + actualEntities);
        System.out.println("expected = " + expected);
        
        assertEquals( EnumSet.of(ValidationMessage.Level.WARNING), actualLevels);
        assertEquals(expected, actualEntities);
        
        System.out.println("/Minimal\n\n");
    }
    
    @Test
    public void validateUnreachableNodesTest_unreachableNodes() throws BadSetInstructionException {
        String code = "(ask: (text: Will this work?)" +
                         "(yes: (reject: No, it won't.))" +
                          "(no: (reject: This shouldn't actually work.)))(>unique1< end)" +
                      "(>shouldNotWork< ask:" +
                      "(text: This should not work, right?)" +
                      "(yes: (reject: it shouldn't work.))" +
                      "(no: (reject: it still shouldn't work.)))" +
                      "(>unique2< end)";
        List<InstructionNodeRef> refs = astParser.graphParser().parse(code);
        fcs = fcsc.parse(refs, "unitName");
        LinkedList<ValidationMessage> messages = instance.validateUnreachableNodes(fcs);
        LinkedList<ValidationMessage> expected = new LinkedList<>();
        expected.addLast(new ValidationMessage(ValidationMessage.Level.WARNING, "Node \"[AskNode id:shouldNotWork title:null]\" is unreachable."));
        expected.addLast(new ValidationMessage(ValidationMessage.Level.WARNING, "Node \"[RejectNode id:$10 title:nullreason=it still shouldn't work.]\" is unreachable."));
        expected.addLast(new ValidationMessage(ValidationMessage.Level.WARNING, "Node \"[RejectNode id:$8 title:nullreason=it shouldn't work.]\" is unreachable."));
        expected.addLast(new ValidationMessage(ValidationMessage.Level.WARNING, "Node \"[EndNode id:unitName-c1-end title:]\" is unreachable."));
        System.out.println("RESULTS: " + messages);
        System.out.println("EXPECTED: " + expected);
        assertEquals(expected, messages);
    }

    
}
