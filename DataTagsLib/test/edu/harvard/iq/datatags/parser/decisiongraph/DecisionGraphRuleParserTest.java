/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.parser.decisiongraph;

import edu.harvard.iq.datatags.parser.decisiongraph.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.NodeHeadRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.RejectNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.TodoNodeRef;
import java.util.Arrays;
import org.codehaus.jparsec.Parser;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class DecisionGraphRuleParserTest {
    
    public DecisionGraphRuleParserTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void nodeHeadNoId() {
        Parser<NodeHeadRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.nodeHead("end") );
        assertEquals( new NodeHeadRef(null, "end"), sut.parse("end") );
        assertEquals( new NodeHeadRef(null, "end"), sut.parse(" end") );
        assertEquals( new NodeHeadRef(null, "end"), sut.parse("end ") );
        assertEquals( new NodeHeadRef(null, "end"), sut.parse(" end ") );
    }
    
    @Test
    public void nodeHeadWithId() {
        Parser<NodeHeadRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.nodeHead("end") );
        assertEquals( new NodeHeadRef("id", "end"), sut.parse(">id<end") );
        assertEquals( new NodeHeadRef("id", "end"), sut.parse(">id< end") );
        assertEquals( new NodeHeadRef("id", "end"), sut.parse(">id<    end") );
        assertEquals( new NodeHeadRef("id", "end"), sut.parse("   >id<    end   ") );
        assertEquals( new NodeHeadRef("177", "end"), sut.parse("   >177<    end   ") );
        assertEquals( new NodeHeadRef("1$$7@7##", "end"), sut.parse("   >1$$7@7##<    end   ") );
        
    }
    
    @Test
    public void endNodeWithId() {
        Parser<EndNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.END_NODE );
        assertEquals( new EndNodeRef("123"), sut.parse("[>123< end]") );
        assertEquals( new EndNodeRef("123"), sut.parse("[ >123< end]") );
        assertEquals( new EndNodeRef("123"), sut.parse("[>123< end ]") );
        assertEquals( new EndNodeRef("123"), sut.parse("[>123<end]") );
        assertEquals( new EndNodeRef("123"), sut.parse("[>123<\nend]") );
        assertEquals( new EndNodeRef("123"), sut.parse("[>123< <-- That's the id? "+ "\nend]") );
    }
    
    @Test
    public void endNodeNoId() {
        Parser<EndNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.END_NODE );
        assertEquals( new EndNodeRef(null), sut.parse("[end]") );
        assertEquals( new EndNodeRef(null), sut.parse("[ end]") );
        assertEquals( new EndNodeRef(null), sut.parse("[ end ]") );
        assertEquals( new EndNodeRef(null), sut.parse("[end ]") );
        assertEquals( new EndNodeRef(null), sut.parse("[\nend]") );
        assertEquals( new EndNodeRef(null), sut.parse("[<-- What, no id? "+ "\nend]") );
    }
    
    @Test
    public void todoNodeWithId() {
        Parser<TodoNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.TODO_NODE );
        assertEquals( new TodoNodeRef("123", "finalize this"), sut.parse("[>123< todo: finalize this]") );
        assertEquals( new TodoNodeRef("123", "finalize this"), sut.parse("[  >123< todo:\nfinalize this  ]") );
        assertEquals( new TodoNodeRef("123", "finalize this"), sut.parse("[>123<todo:finalize this]") );
    }
    
    @Test
    public void todoNodeNoId() {
        Parser<TodoNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.TODO_NODE );
        assertEquals( new TodoNodeRef(null, "finalize this"), sut.parse("[ todo: finalize this]") );
        assertEquals( new TodoNodeRef(null, "finalize this"), sut.parse("[todo:\nfinalize this]") );
        assertEquals( new TodoNodeRef(null, "finalize this"), sut.parse("[todo:finalize this]") );
    }
    
    @Test
    public void rejectNodeWithId() {
        Parser<RejectNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.REJECT_NODE );
        assertEquals( new RejectNodeRef("123", "finalize this"), sut.parse("[>123< reject: finalize this]") );
        assertEquals( new RejectNodeRef("123", "finalize this"), sut.parse("[  >123< reject:\nfinalize this  ]") );
        assertEquals( new RejectNodeRef("123", "finalize this"), sut.parse("[>123<reject:finalize this]") );
    }
    
    @Test
    public void rejectNodeNoId() {
        Parser<RejectNodeRef> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.REJECT_NODE );
        assertEquals( new RejectNodeRef(null, "finalize this"), sut.parse("[ reject: finalize this]") );
        assertEquals( new RejectNodeRef(null, "finalize this"), sut.parse("[reject:\nfinalize this]") );
        assertEquals( new RejectNodeRef(null, "finalize this"), sut.parse("[reject:finalize this\n\n\n]") );

        assertEquals( new RejectNodeRef(null, "finalize\nthis"), sut.parse("[ reject: finalize\nthis]") );
        assertEquals( new RejectNodeRef(null, "finalize 16 of these !@#!%!#$!$"), sut.parse("[ reject: finalize 16 of these !@#!%!#$!$]") );
    }
    
    @Test
    public void atomicAssignmentSlotTest() {
        Parser<SetNodeRef.AtomicAssignment> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.ATOMIC_ASSIGNMENT_SLOT );
        
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("s"),"v"), sut.parse("s=v"));
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("s"),"v"), sut.parse("s =v"));
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("s"),"v"), sut.parse("s= v"));
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("s"),"v"), sut.parse("s = v"));
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("top","mid","bottom"),"aValue"), sut.parse("top/mid/bottom=aValue"));
        assertEquals( new SetNodeRef.AtomicAssignment(Arrays.asList("top","mid","bottom"),"aValue"),
                                                                        sut.parse("top / mid / bottom = aValue"));
        
    }
    
    @Test
    public void aggregateAssignmentSlotTest() {
        Parser<SetNodeRef.AggregateAssignment> sut = DecisionGraphTerminalParser.buildParser( DecisionGraphRuleParser.AGGREGATE_ASSIGNMENT_SLOT );
        
        assertEquals( new SetNodeRef.AggregateAssignment(Arrays.asList("s"), Arrays.asList("v")),
                sut.parse("s+=v"));
        assertEquals( new SetNodeRef.AggregateAssignment(Arrays.asList("top","mid","bottom"),
                                                         Arrays.asList("val1", "val2", "val3")),
                sut.parse("top/mid/bottom+=val1, val2, val3"));
        
    }
}
