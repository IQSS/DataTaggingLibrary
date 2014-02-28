/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeHeadRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import edu.harvard.iq.datatags.parser.flowcharts.references.TermNodeRef;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Parsers;
import org.codehaus.jparsec.Scanners;
import org.codehaus.jparsec.error.ParserException;
import org.codehaus.jparsec.functors.Pair;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class FlowChartSetASTParserTest {
	
	FlowChartSetASTParser instance;
			
	public FlowChartSetASTParserTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
		instance = new FlowChartSetASTParser();
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
	public void testNodeHead() {
		Parser<NodeHeadRef> subParser = instance.nodeHead();
		assertEquals( new NodeHeadRef("setNode", NodeType.Set), 
                      subParser.parse(">setNode<set") );
		assertEquals( new NodeHeadRef("setNode", NodeType.Set), 
                      subParser.parse(">setNode< set") );
		assertEquals( new NodeHeadRef(null, NodeType.Call), subParser.parse("call") );
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
    public void testTermNode() {
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(simpleTerm:simpleExplanation)"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(simpleTerm : simpleExplanation)"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(   simpleTerm : simpleExplanation   )"));
        assertEquals( new TermNodeRef("simpleTerm", "simpleExplanation"),
                      instance.termNode().parse("(   simpleTerm :  \n\t\tsimpleExplanation   )"));
        String longExplanation = "lorem ipsum dolor sit amet. lorem ipsum 0183274018375  AAIIrejrgwhgootutuhwo Many Chars\n\t+_!@)$*!@$&^@$()^@&$%!(*$_!#(!%^!^&$#_%^*(&#_%(^*@?>||}}?{<";
        
        assertEquals( new TermNodeRef("multi word Term", longExplanation),
                      instance.termNode().parse("(   multi word Term : " + longExplanation + ")"));
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
        assertEquals( "test", instance.completeNode( Scanners.ANY_CHAR.many().source()).parse("(test)") );
        assertEquals( "test", instance.completeNode( Scanners.ANY_CHAR.many().source()).parse("( test)") );
        assertEquals( "test", instance.completeNode( Scanners.ANY_CHAR.many().source()).parse("(    test)") );
    }
    
    @Test
    public void testInstcutionNode() {
        assertEquals( new InstructionNodeRef( new NodeHeadRef("id",NodeType.End)),
                      instance.instructionNode().parse("(>id< end)"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef("id",NodeType.End)),
                      instance.instructionNode().parse("( >id< end  )"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef("id",NodeType.End)),
                      instance.instructionNode().parse("(>id<end)"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef(null,NodeType.End)),
                      instance.instructionNode().parse("(end)"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef(null,NodeType.End)),
                      instance.instructionNode().parse("(end  )"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef(null,NodeType.End)),
                      instance.instructionNode().parse("(   end)"));
        assertEquals( new InstructionNodeRef( new NodeHeadRef(null,NodeType.End)),
                      instance.instructionNode().parse("(   end  )"));
    }
    
	@Test
	public void testSample() {
        Parser<Pair<String,String>> sut = Parsers.tuple(Scanners.IDENTIFIER.followedBy(Scanners.among(":")),
            Scanners.ANY_CHAR.many().source()
            ).reluctantBetween(instance.startNode(), instance.endNode());

        Pair<String, String> value = sut.parse("(hello:world))");

        assertEquals( new Pair<>("hello","world)"), value );
        
        Parser<Pair<String, String>> tupleParser = Parsers.tuple( Scanners.notChar(':').many().source().followedBy(Scanners.among(":")),
                Scanners.ANY_CHAR.many().source());
        assertEquals( new Pair<>("hello","world"), tupleParser.parse("hello:world") );
	}
	
}
