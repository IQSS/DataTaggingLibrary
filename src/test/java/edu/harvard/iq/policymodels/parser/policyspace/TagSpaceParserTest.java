package edu.harvard.iq.policymodels.parser.policyspace;

import edu.harvard.iq.policymodels.parser.policyspace.TagSpaceParseResult;
import edu.harvard.iq.policymodels.parser.policyspace.TagSpaceParser;
import edu.harvard.iq.policymodels.model.policyspace.slots.AggregateSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class TagSpaceParserTest {
    
    public TagSpaceParserTest() {
    }

    @Test
    public void testParentType() throws Exception {
         String typeDef
                = "top: consists of agg, ato.\n"
                + "agg: some of A, B, C.\n"
                + "ato: one of X, Y, Z.";

        TagSpaceParseResult parse = new TagSpaceParser().parse(typeDef);
        CompoundSlot topType = parse.buildType("top").get();
        AggregateSlot agg = (AggregateSlot) topType.getSubSlot("agg");
        assertEquals( agg, agg.getItemType().getParentSlot() );
        
        AtomicSlot ato = (AtomicSlot) topType.getSubSlot("ato");
        assertNull( ato.getParentSlot() );

    }
}
