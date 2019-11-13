package edu.harvard.iq.policymodels.parser.decisiongraph;

import edu.harvard.iq.policymodels.parser.decisiongraph.DecisionGraphTerminalParser;
import org.jparsec.Tokens;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class DecisionGraphTerminalParserTest {

    @Test
    public void testNodeValueId() {
        String nodeIdParseRes = DecisionGraphTerminalParser.NODE_ID_VALUE.parse("abc");
        Assert.assertEquals("abc",nodeIdParseRes);
    }
    
    @Test
    public void testNodeId() {
        Tokens.Fragment nodeIdParseRes = DecisionGraphTerminalParser.NODE_ID.parse(">abc<");
        Assert.assertEquals("abc",nodeIdParseRes.text());
    }
    
}
