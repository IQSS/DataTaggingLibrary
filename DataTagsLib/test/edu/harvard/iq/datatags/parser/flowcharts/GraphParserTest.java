package edu.harvard.iq.datatags.parser.flowcharts;

import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeRefVizalizer;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Arrays;
import java.util.List;
import org.codehaus.jparsec.Parser;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 * Test suite for the graph parser. Separate from  {@link FlowChartSetASTParserTest}
 * Since the tests are holistic, rather than per-parser.
 * @author Michael Bar-Sinai
 */
public class GraphParserTest {
    FlowChartASTParser astParser;
    Parser<List<InstructionNodeRef>> sut;
	
	@Before
	public void setUp() {
        astParser = new FlowChartASTParser();
		sut = astParser.graphParser();
	}
	
	@Test
	public void testNonRec() {
		String code = "(call: hello)(call: world)(end)";
        List<InstructionNodeRef> expected = 
                Arrays.asList(
                        new CallNodeRef("hello"),
                        new CallNodeRef("world"),
                        new EndNodeRef() 
                );
        assertEquals( expected, sut.parse(code) );
	}
	
    @Test
	public void testSingleRec() throws IOException {
		String code = "(todo)(>id< ask:(>T< text:here's a question)(>y< yes:(>e< end)))";
        List<InstructionNodeRef> res = sut.parse(code);
        
        GraphvizGraphNodeRefVizalizer v = new GraphvizGraphNodeRefVizalizer(res);
        v.visualize( new OutputStreamWriter(System.out) );
        
	}
    
}
