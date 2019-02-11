package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.parser.tagspace.ast.AbstractAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.AggregateAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.AtomicAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompoundAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.ToDoAstSlot;
import edu.harvard.iq.datatags.parser.tagspace.ast.ValueDefinition;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.jparsec.Parser;
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
public class TagSpaceRuleParserTest {
    
    public TagSpaceRuleParserTest() {
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
    public void testValueDefinition() {
        Parser<ValueDefinition> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.VALUE_DEFINITION_RULE );
        assertEquals( new ValueDefinition("clear", ""), sut.parse("clear"));
        
        ValueDefinition expected = new ValueDefinition("clear", "a value that is not encrypted at all");
        assertEquals( expected, sut.parse("clear [a value that is not encrypted at all]") );
        assertEquals( expected, 
                         sut.parse("clear [a value that is not encrypted at all] <-- This is a line comment") );
        assertEquals( expected, sut.parse("<*un*>clear [a value that is not encrypted at all]") );
    }
    
    @Test
    public void testAtomicRule() {
        Parser<AtomicAstSlot> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.ATOMIC_SLOT_RULE );
        List<ValueDefinition> valueDefinitions = Arrays.asList( vDef("clear",""), vDef("encrypt",""),
                        vDef("clientSide",""), vDef("doubleEncrypt",""));
        
        assertEquals(new AtomicAstSlot("Transfer", "", valueDefinitions ),
                      sut.parse("Transfer: one of clear, encrypt, clientSide, doubleEncrypt.") );
        assertEquals(new AtomicAstSlot("Transfer", "How we transfer the data", valueDefinitions ),
                      sut.parse("Transfer [How we transfer the data]: one of clear, encrypt, clientSide, doubleEncrypt.") );
        assertEquals(new AtomicAstSlot("Transfer", "How we transfer the data", valueDefinitions ),
                      sut.parse("Transfer [How we transfer the data]<* err, what?*>: one of clear, encrypt, clientSide, doubleEncrypt. <--- la la la") );
    }
   
    @Test
    public void testAggregateRule() {
        Parser<AggregateAstSlot> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.AGGREGATE_SLOT_RULE );
        List<ValueDefinition> valueDefinitions = new LinkedList<>(Arrays.asList( vDef("clear",""), vDef("encrypt",""),
                        vDef("clientSide",""), vDef("doubleEncrypt","")));
        
        assertEquals(new AggregateAstSlot("Transfer", "", valueDefinitions ),
                      sut.parse("Transfer: some of clear, encrypt, clientSide, doubleEncrypt.") );
        assertEquals(new AggregateAstSlot("Transfer", "How we transfer the data", valueDefinitions ),
                      sut.parse("Transfer [How we transfer the data]: some of clear, encrypt, clientSide, doubleEncrypt.") );
        assertEquals(new AggregateAstSlot("Transfer", "How we transfer the data", valueDefinitions ),
                      sut.parse("Transfer [How we transfer the data]<* err, what?*>: some of clear, encrypt, clientSide, doubleEncrypt. <--- la la la") );
        
        valueDefinitions.add( new ValueDefinition("superEncrypt", "does not really exist but sounds good") );
        assertEquals(new AggregateAstSlot("Transfer", "How we transfer the data", valueDefinitions ),
                      sut.parse("Transfer [How we transfer the data]: some of clear, encrypt, clientSide, doubleEncrypt, superEncrypt [does not really exist but sounds good].") );
    }
    
    @Test
    public void testCompoundRule() {
        Parser<CompoundAstSlot> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.COMPOUND_SLOT_RULE );
        List<String> subSlots = Arrays.asList( "HIPAA", "FERPA", "PrivacyAct","JUnit");
        
        assertEquals(new CompoundAstSlot("Legal", "", subSlots ),
                      sut.parse("Legal: consists of HIPAA, FERPA, PrivacyAct, JUnit.") );
        assertEquals(new CompoundAstSlot("Legal", "Aspects of the dataset by act", subSlots ),
                      sut.parse("Legal [Aspects of the dataset by act]: consists of HIPAA, FERPA, PrivacyAct, JUnit.") );
        assertEquals(new CompoundAstSlot("Legal", "Aspects of the dataset by act", subSlots ),
                      sut.parse("Legal<*please ignore me!*> [Aspects of the dataset by act]: consists of HIPAA, FERPA, PrivacyAct, JUnit. <--- la la la") );
    }
    
    @Test
    public void testTodoRule() {
        Parser<ToDoAstSlot> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.TODO_RULE );
        assertEquals(new ToDoAstSlot("Procrastination", ""), sut.parse("Procrastination: TODO.") );
        assertEquals(new ToDoAstSlot("Procrastination", ""), sut.parse("Procrastination: TODO   .") );
        assertEquals(new ToDoAstSlot("Procrastination", ""), sut.parse("Procrastination: TODO   \n.") );
        assertEquals(new ToDoAstSlot("Procrastination", ""), sut.parse("\t \t Procrastination: \n \t \t \tTODO<*!!!*>   \n.") );
        assertEquals(new ToDoAstSlot("Procrastination", "I'll get to this later"), sut.parse("Procrastination [I'll get to this later]: TODO.") );
        assertEquals(new ToDoAstSlot("Procrastination", "I'll get to this later"), sut.parse("Procrastination [I'll get to this later] <--- Yeah, right\n\t: TODO.") );
    }
    
    @Test
    public void testRule() {
        Parser<? extends AbstractAstSlot> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.RULE );
        assertEquals(new ToDoAstSlot("Test",""), sut.parse("Test: TODO."));
        assertEquals(new AtomicAstSlot("Test","a test slot", Arrays.asList(vDef("A"), vDef("B","value b"))),
                            sut.parse("Test [a test slot]: one of A, B[value b]."));
        assertEquals(new AggregateAstSlot("Test", "", Arrays.asList(vDef("A"), vDef("B","value b"))),
                            sut.parse("Test: some of A, B[value b]."));
        assertEquals(new CompoundAstSlot("Test","", Arrays.asList("A","B","C")),
                            sut.parse("Test: consists of A, B, C."));
    }
    
    @Test
    public void testRules() {
        Parser<List<? extends AbstractAstSlot>> sut = TagSpaceTerminalParser.buildParser( TagSpaceRuleParser.RULES );
        
        List<? extends AbstractAstSlot> expected = Arrays.asList(new ToDoAstSlot("ATodoNode",""),
                    new AtomicAstSlot("AnAtomicSlot","one value", Arrays.asList(vDef("I"), vDef("II"), vDef("III","one two three")) ),
                new AggregateAstSlot("AnAggregateSlot","multi value", Arrays.asList(vDef("I"), vDef("II"), vDef("III","one two three")) )
        );
        assertEquals( expected,
                sut.parse("ATodoNode: TODO. AnAtomicSlot [one value]: one of I, II, III[one two three]. AnAggregateSlot [multi value]: some of I, II, III[one two three]."));
        assertEquals( expected,
                sut.parse("ATodoNode: TODO. \n"
                        + "AnAtomicSlot [one value]: one of I, II, III[one two three]. \n"
                        + "AnAggregateSlot [multi value]: some of I, II, III[one two three]."));
        assertEquals( expected,
                sut.parse("ATodoNode: TODO. <--- we'll have to do this at some point\n"
                        + "AnAtomicSlot [one value]: one of I, II, III[one two three]. \n"
                        + "<*\n"
                        + " * While I realize this is \n"
                        + " * A sample text that going to be ignored\n"
                        + " * I'm not going to lorem ipsum my way out of it!\n"
                        + "*>\n"
                        + "AnAggregateSlot [multi value]: some of I, II, III[one two three]."));
    }
    
    private static ValueDefinition vDef(String name) {
        return vDef(name, "");
    }
    
    private static ValueDefinition vDef(String name, String note) {
        return new ValueDefinition(name, note);
    }
}

