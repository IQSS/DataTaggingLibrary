package edu.harvard.iq.datatags.parser.tagspace;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
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
        AggregateSlot agg = (AggregateSlot) topType.getTypeNamed("agg");
        assertEquals( agg, agg.getItemType().getParentSlot() );
        
        AtomicSlot ato = (AtomicSlot) topType.getTypeNamed("ato");
        assertNull( ato.getParentSlot() );

    }
}
