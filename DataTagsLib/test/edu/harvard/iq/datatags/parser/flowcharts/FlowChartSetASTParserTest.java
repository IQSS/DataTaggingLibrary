/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.flowcharts.references.NodeHeadReference;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.error.ParserException;
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
		Parser<NodeHeadReference> subParser = instance.nodeHead();
		assertEquals( new NodeHeadReference("setNode", NodeType.Set), subParser.parse("setNode set") );
		assertEquals( new NodeHeadReference(null, NodeType.Call), subParser.parse("call") );
	}

	@Test( expected=ParserException.class)
	public void testNodeType_fail() {
		Parser<NodeType> subParser = instance.nodeType();
		subParser.parse("not-a-node-type");
	}
	
	@Test
	public void testNodeId() {
		Parser<String> sut = instance.nodeId();
		assertEquals("hello", sut.parse("hello") );
		assertEquals("@@hello", sut.parse("@@hello") );
		assertEquals("!@$%", sut.parse("!@$%") );
	}
	
	@Test(expected = ParserException.class)
	public void testNodeId_fail() {
		Parser<String> sut = instance.nodeId();
		assertEquals("hello", sut.parse("hello world") );
	}
	
}
