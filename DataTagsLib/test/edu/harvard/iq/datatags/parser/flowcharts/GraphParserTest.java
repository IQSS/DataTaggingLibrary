package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeType;
import edu.harvard.iq.datatags.parser.flowcharts.references.TypedNodeHeadRef;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeRefVizalizer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jparsec.Parser;
import org.codehaus.jparsec.Scanners;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the graph parser. Separate from  {@link FlowChartSetASTParserTest}
 * Since the tests are holistic, rather than per-parser.
 * @author Michael Bar-Sinai
 */
public class GraphParserTest {
    FlowChartSetASTParser astParser;
    Parser<List<InstructionNodeRef>> sut;
	
	@Before
	public void setUp() {
        astParser = new FlowChartSetASTParser();
		sut = astParser.graphParser();
	}
	
	@Test
	public void testNonRec() {
		String code = "(call: hello)(call: world)(end)";
        List<InstructionNodeRef> expected = 
                Arrays.asList(
                        new CallNodeRef(new TypedNodeHeadRef(null, NodeType.Call), "hello"),
                        new CallNodeRef(new TypedNodeHeadRef(null, NodeType.Call), "world"),
                        new InstructionNodeRef( new TypedNodeHeadRef(null, NodeType.End) ) 
                );
        assertEquals( expected, sut.parse(code) );
	}
	
    @Test
	public void testSingleRec() throws IOException {
		String code = "(ask:(text:question)(yes:(end))(no:(end)))";
        List<InstructionNodeRef> res = sut.parse(code);
        GraphvizGraphNodeRefVizalizer v = new GraphvizGraphNodeRefVizalizer(res);
        v.visualize( new OutputStreamWriter(System.out) );
        
	}
    
    @Test
    public void testMany() {
        List<String> expected = Arrays.asList("A","A","A");
        assertEquals( expected, Scanners.isChar('A').source().many().parse("AAA"));
        List<?> ex2 = Arrays.asList( new InstructionNodeRef( new TypedNodeHeadRef(null, NodeType.End)),
                new InstructionNodeRef( new TypedNodeHeadRef(null, NodeType.End)),
                new InstructionNodeRef( new TypedNodeHeadRef(null, NodeType.End)));
        assertEquals( ex2, astParser.endNode().many().parse("(end)(end)(end)"));
    }
}
