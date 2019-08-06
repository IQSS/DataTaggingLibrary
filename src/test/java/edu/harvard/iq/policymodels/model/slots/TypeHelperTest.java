
/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.policymodels.model.slots;

import edu.harvard.iq.policymodels.model.policyspace.slots.CompoundSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.slots.SlotHelper;
import edu.harvard.iq.policymodels.model.policyspace.slots.AtomicSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import edu.harvard.iq.policymodels.parser.policyspace.TagSpaceParser;
import edu.harvard.iq.policymodels.parser.exceptions.DataTagsParseException;
import java.util.Arrays;
import java.util.List;
import org.junit.After;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class TypeHelperTest {
    
    public TypeHelperTest() {
    }
    
    AbstractSlot dataTagsType;
    
    @Before
    public void setUp() throws DataTagsParseException {
        String source = "DataTags: consists of color, meal, styles, nextThing.\n"
                + "nextThing: TODO.\n"
                + "color: one of red, green, blue, yellow.\n"
                + "styles: some of HipHip, Jazz, Blues, RockAndRoll.\n"
                + "meal: consists of open, main, desert.\n"
                + "open: one of salad, soup, beetle.\n"
                + "main: some of meat, rice, potato, lettuce.\n"
                + "desert: one of iceCream, chocolate, appleSauce.\n"
                ;
        
        dataTagsType = new TagSpaceParser().parse(source).buildType("DataTags").get();
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of safeGet method, of class TypeHelper.
     */
    @Test
    public void testSafeGet_simple() {
        AtomicSlot colorType = (AtomicSlot)((CompoundSlot)dataTagsType).getSubSlot("color");
        AbstractValue red = SlotHelper.getCreateValue(colorType, "red", "");
        assertEquals(red, SlotHelper.safeGet(dataTagsType, "color", "red") );
    }
    
    
    @Test
    public void testPathFormat() {
        List<String> sample = Arrays.asList("Hello","World","What's","Up");
        assertEquals("Hello/World/What's/Up", SlotHelper.formatSlotPath(sample));
    }
    
}
