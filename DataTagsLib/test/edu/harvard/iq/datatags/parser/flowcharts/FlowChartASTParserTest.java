package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TypedNodeHeadRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.StringNodeHeadRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TextNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.error.ParserException;
import org.codehaus.jparsec.functors.Pair;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class FlowChartASTParserTest {
	
	FlowChartASTParser instance;
			
	public FlowChartASTParserTest() {
	}
	
	@Before
	public void setUp() {
		instance = new FlowChartASTParser();
	}
	
	@After
	public void tearDown() {
	}

	@Test
	public void testNodeType() {
		Parser<NodeType> subParser = instance.nodeType();
		for ( NodeType nd: NodeType.values() ) {
			assertEquals( nd, subParser.parse( nd.name().toLowerCase() ) );
		}
	}
	
    @Test
    public void testAskNode_call()  {
        AnswerNodeRef yesRef = new AnswerNodeRef(new StringNodeHeadRef(null, "yes"), 
                                    Collections.singletonList( new CallNodeRef((String)null, "yesImpl")));
        AskNodeRef expected = new AskNodeRef("id", new TextNodeRef("theText", "here's a question"), 
                                            new LinkedList(), 
                                            Collections.singletonList(yesRef));
        String code = "(>id< ask:(>theText< text:here's a question)(yes:(call:yesImpl)))";
        Parser<AskNodeRef> sut = instance.askNode( instance.graphParser() );
        assertEquals( expected, sut.parse(code) );
    }
    
    @Test
    public void testCallNode_trim()  {
        CallNodeRef expected = new CallNodeRef("node");
        Parser<CallNodeRef> sut = instance.callNode();
        assertEquals( expected, sut.parse("(call: node )") );
        assertEquals( expected, sut.parse("(call: node)") );
        assertEquals( expected, sut.parse("(call:     node      )") );
    }
    
    @Test
    public void testAskNode_end()  {
        AnswerNodeRef yesRef = new AnswerNodeRef(new StringNodeHeadRef(null, "yes"), 
                                    Collections.singletonList( new EndNodeRef()));
        AskNodeRef expected = new AskNodeRef("id", new TextNodeRef(null, "here's a question"), 
                                            new LinkedList(), 
                                            Collections.singletonList(yesRef));
        String code = "(>id< ask:(text:here's a question)(yes:(end)))";
        Parser<AskNodeRef> sut = instance.askNode( instance.graphParser() );
        assertEquals( expected, sut.parse(code) );
    }
    
    @Test
    public void testAskNode_noId()  {
        AnswerNodeRef yesRef = new AnswerNodeRef(new StringNodeHeadRef(null, "yes"), 
                                    Collections.singletonList( new EndNodeRef()));
        AskNodeRef expected = new AskNodeRef(null, new TextNodeRef(null, "here's a question"), 
                                            new LinkedList(), 
                                            Collections.singletonList(yesRef));
        String code = "(ask:(text:here's a question)(yes:(end)))";
        Parser<AskNodeRef> sut = instance.askNode( instance.graphParser() );
        assertEquals( expected, sut.parse(code) );
    }
    
    @Test
    public void testAskNode_terms()  {
        AnswerNodeRef yesRef = new AnswerNodeRef(new StringNodeHeadRef(null, "yes"), 
                                    Collections.singletonList( new EndNodeRef()));
        List<TermNodeRef> terms = Collections.singletonList(new TermNodeRef("zerm","explanation") );
        AskNodeRef expected = new AskNodeRef(null, new TextNodeRef(null, "here's a question"), 
                                            terms, 
                                            Collections.singletonList(yesRef));
        String code = "(ask:(text:here's a question)(terms:(zerm:explanation))(yes:(end)))";
        
        Parser<AskNodeRef> sut = instance.askNode( instance.graphParser() );
        assertEquals( expected, sut.parse(code) );
    }
    
    @Test
    public void askNodeQuestionPartTest() {
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> sut = instance.askNodeQuestionPart();
        List<TermNodeRef> terms = Collections.singletonList(new TermNodeRef("zerm","explanation") );
        Pair<TextNodeRef, List<TermNodeRef>> expected = new Pair<>( new TextNodeRef(null, "some text"), terms );
        
        assertEquals(expected, sut.parse("(text:some text)(terms:(zerm:explanation))"));
    }
    
    @Test
    public void askNodeQuestionPartTest_noTerms() {
        Parser<Pair<TextNodeRef, List<TermNodeRef>>> sut = instance.askNodeQuestionPart();
        Pair<TextNodeRef, List<TermNodeRef>> expected = new Pair<>( new TextNodeRef(null, "some text"), Collections.<TermNodeRef>emptyList() );
        
        assertEquals(expected, sut.parse("(text:some text)"));
    }
    
    @Test
    public void textNodeTest() {
        List<TextNodeRef> nodes = Arrays.asList( new TextNodeRef("id1", "text1"), new TextNodeRef("id2", "text2") );
        assertEquals( nodes, instance.textNode().many().parse("(>id1< text:text1)(>id2< text:text2)"));
        nodes = Arrays.asList( new TextNodeRef(null, "text1"), new TextNodeRef(null, "text2") );
        assertEquals( nodes, instance.textNode().many().parse("(text:text1)(text:text2)"));
    }
    
	@Test
	public void testNodeHead() {
		Parser<TypedNodeHeadRef> subParser = instance.typedNodeHead();
		assertEquals( new TypedNodeHeadRef("setNode", NodeType.Set), 
                      subParser.parse(">setNode<set") );
		assertEquals( new TypedNodeHeadRef("setNode", NodeType.Set), 
                      subParser.parse(">setNode< set") );
		assertEquals( new TypedNodeHeadRef(null, NodeType.Call), subParser.parse("call") );
	}
    
    @Test
    public void testSimpleNode() {
        Parser<CallNodeRef> sut = instance.callNode();
        
        assertEquals( new CallNodeRef( null, "Some other id"),
                      sut.parse("(call: Some other id)"));
        assertEquals( new CallNodeRef( "call-other", "Some other id"),
                      sut.parse("(>call-other< call: Some other id)"));
    }
    
	@Test( expected=ParserException.class)
	public void testNodeType_fail() {
		Parser<NodeType> subParser = instance.nodeType();
		subParser.parse("not-a-node-type");
	}
	
	@Test
	public void testNodeId() {
		Parser<String> sut = instance.nodeId();
		assertEquals("hello", sut.parse(">hello<") );
		assertEquals("hello", sut.parse(">  hello    <") );
		assertEquals("@@hello", sut.parse(">@@hello<") );
		assertEquals("!@$%", sut.parse(">!@$%<") );
		assertEquals("hello, world! This is indeed a very long id for a node",
                     sut.parse(">hello, world! This is indeed a very long id for a node<") );
	}
	
	@Test(expected = ParserException.class)
	public void testNodeId_fail() {
		Parser<String> sut = instance.nodeId();
		assertEquals("hello", sut.parse("hello world") );
	}
	
    @Test
    public void testSetAssignment() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( new Pair<>("hello","world"), sut.parse("hello=world"));
        assertEquals( new Pair<>("this/hello","world"), sut.parse("this/hello=world"));
        assertEquals( new Pair<>("that/this/hello","world"), sut.parse("that/this/hello=world"));
    }

    @Test( expected = ParserException.class )
    public void testSetAssignment_fail1() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( null, sut.parse("hello="));
    }
    @Test( expected = ParserException.class )
    public void testSetAssignment_fail2() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( null, sut.parse("=world"));
    }
    @Test( expected = ParserException.class )
    public void testSetAssignment_fail3() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( null, sut.parse("/=world"));
    }
    @Test( expected = ParserException.class )
    public void testSetAssignment_fail4() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( null, sut.parse("hello/=world"));
    }
    @Test( expected = ParserException.class )
    public void testSetAssignment_fail5() {
        Parser<Pair<String, String>> sut = instance.setAssignmentPair();
        assertEquals( null, sut.parse("/hello=world"));
    }
    
    @Test
    public void testSlotReferece() {
        Parser<String> sut = instance.slotReference();
        assertEquals("hello", sut.parse("hello"));
    }
    
    @Test
    public void testTermNode() {
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(simpleTerm:simpleExplanation)"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(simpleTerm : simpleExplanation)"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(   simpleTerm : simpleExplanation   )"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(   simpleTerm :  \n\t\tsimpleExplanation   )"));
        String longExplanation = "lorem ipsum dolor sit amet. lorem ipsum 0183274018375  AAIIrejrgwhgootutuhwo Many Chars\n\t+_!@$*!@$&^@$^@&$%!*$_!!%^!^&$#_%^*&#_%^*@?>||}}?{<";
        
        assertEquals( new TermNodeRef("multi word Term", longExplanation),
                      instance.termNode().parse("(   multi word Term : " + longExplanation + ")"));
    }
    
    @Test
    public void testTermsNode() {
        Parser<List<TermNodeRef>> sut = instance.termsNode();
        String sourceCnd ="(terms:(data: 0s and 1s in some structured way)(personal information: explanation about pi))";
        TermNodeRef dataRef = new TermNodeRef("data", "0s and 1s in some structured way");
        TermNodeRef piRef = new TermNodeRef("personal information", "explanation about pi");
        List<TermNodeRef> expected = Arrays.asList(dataRef, piRef);
        assertEquals( expected, sut.parse(sourceCnd) );
        String sourceWithWhiteSpace ="(terms:\n\t(data: 0s and 1s in some structured way)\n\t(personal information: explanation about pi)\n)";
        assertEquals( expected, sut.parse(sourceWithWhiteSpace) );
    }
    
    @Test( expected=ParserException.class )
    public void testTermNode_fail() {
        assertEquals( new TermNodeRef("we won't get here", "hopefully"),
                      instance.termNode().parse("(simpleTerm)"));
        assertEquals( new TermNodeRef("we won't get here", "hopefully"),
                      instance.termNode().parse("(:simpleTerm)"));
        assertEquals( new TermNodeRef("we won't get here", "hopefully"),
                      instance.termNode().parse("(abc:simpleTerm"));
    }
    
    @Test
    public void testCompleteNode() {
        assertEquals( "test", instance.completeNode( Scanners.notChar(')').many().source() ).parse("(test)") );
        assertEquals( "test", instance.completeNode( Scanners.notChar(')').many().source() ).parse("( test)") );
        assertEquals( "test", instance.completeNode( Scanners.notChar(')').many().source() ).parse("(    test)") );
    }
    
    @Test
    public void testEndNode() {
        assertEquals( new EndNodeRef("this is the"), instance.endNode().parse("(>this is the< end)") );
        assertEquals( new EndNodeRef(null), instance.endNode().parse("(end)") );
    }
    
    @Test( expected=ParserException.class )
    public void testEndNode_reject() {
        assertEquals( new EndNodeRef("this is the"), instance.endNode().parse("(>this is the< todo)") );
    }
    
    @Test( expected=ParserException.class )
    public void testEndNode_reject2() {
        assertEquals( new EndNodeRef("this is the"), instance.endNode().parse("(todo)") );
    }
    
    @Test
    public void testTodoNode() {
        assertEquals( new TodoNodeRef(null, null), instance.todoNode().parse("(todo)") );
        assertEquals( new TodoNodeRef("things", null), instance.todoNode().parse("(>things< todo)") );
        assertEquals( new TodoNodeRef("things", null), instance.todoNode().parse("(>things< todo )") );
        assertEquals( new TodoNodeRef("things", "This is stuff that needs to be done. Pronto."), instance.todoNode().parse("(>things< todo: This is stuff that needs to be done. Pronto.)") );
    }
    
    @Test
    public void testSetNode() {
        SetNodeRef expected = new SetNodeRef( new TypedNodeHeadRef("set-id", NodeType.Set) );
        expected.addAssignment("slot1", "value1");
        
        Parser<SetNodeRef> sut = instance.setNode();
        assertEquals( expected, sut.parse("(>set-id< set: slot1=value1)"));
        
        expected.addAssignment("slot/number/two", "value2");
        assertEquals( expected, sut.parse("(>set-id< set: slot1=value1,slot/number/two=value2)"));
        assertEquals( expected, sut.parse("(>set-id< set: slot1=value1, slot/number/two=value2)"));
        assertEquals( expected, sut.parse("(>set-id< set: slot1 = value1, slot/number/two = value2)"));
        assertEquals( expected, sut.parse("(>set-id< set: slot1 = value1,\n\t\tslot/number/two = value2)"));
        
        expected = new SetNodeRef( new TypedNodeHeadRef(null, NodeType.Set) );
        expected.addAssignment("slot1", "value1");
        expected.addAssignment("slot/number/two", "value2");
        assertEquals( expected, sut.parse("(set: slot1=value1,slot/number/two=value2)"));
    }
    
//	@Test
//	public void testSample() {
//        Parser<Pair<TextNodeRef, List<TermNodeRef>>> textTerm = 
//                Parsers.tuple(instance.textNode().followedBy(whitespaces),instance.termsNode());
//        TextNodeRef tnr = new TextNodeRef(null, "xxxxx");
//        TermNodeRef anr = new TermNodeRef("a","AAAA");
//        TermNodeRef bnr = new TermNodeRef("b","BBBB");
//        assertEquals( new Pair<>(tnr, Arrays.asList(anr,bnr)),
//                      textTerm.parse("(text:xxxxx)(terms:(a:AAAA)(b:BBBB))") );
//        assertEquals( new Pair<>(tnr, Arrays.asList(anr,bnr)),
//                      textTerm.parse("(text:xxxxx)   (terms:(a:AAAA)(b:BBBB))") );
//        
//        Map<TextNodeRef, Pair<TextNodeRef, List<TermNodeRef>>> t2p = new Map<TextNodeRef, Pair<TextNodeRef, List<TermNodeRef>>>(){
//
//            @Override
//            public Pair<TextNodeRef, List<TermNodeRef>> map(TextNodeRef from) {
//                return new Pair<>(from, Collections.<TermNodeRef>emptyList());
//            }
//        };
//        Parser<Pair<TextNodeRef, List<TermNodeRef>>> textOnly = instance.textNode().map(t2p);
//        
//        Parser<Pair<TextNodeRef, List<TermNodeRef>>> sutOrText = textTerm.or( textOnly );
//        assertEquals( new Pair<>(tnr, Arrays.asList(anr,bnr)),
//                      sutOrText.parse("(text:xxxxx)(terms:(a:AAAA)(b:BBBB))") );
//        assertEquals( new Pair<>(tnr, Arrays.asList(anr,bnr)),
//                      sutOrText.parse("(text:xxxxx)   (terms:(a:AAAA)(b:BBBB))") );
//        
//	}
	
}
