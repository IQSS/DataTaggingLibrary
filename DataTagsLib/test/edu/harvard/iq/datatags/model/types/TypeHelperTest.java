/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
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
    
    TagType dataTagsType;
    
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
        AtomicType colorType = (AtomicType)((CompoundType)dataTagsType).getTypeNamed("color");
        TagValue red = TypeHelper.getCreateValue(colorType, "red", "");
        assertEquals( red, TypeHelper.safeGet(dataTagsType, "color", "red") );
    }
    
    
}
