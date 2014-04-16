/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.DataTags;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.Map;
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
public class StringMapFormatTest {
    
    public StringMapFormatTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    DataTags tags;
    
    @Before
    public void setUp() throws DataTagsParseException {
        String source = "DataTags: color, meal, styles, nextThing.\n"
                + "color: one of red, green, blue, yellow.\n"
                + "styles: some of HipHip, Jazz, Blues, RockAndRoll.\n"
                + "meal: open, main, desert.\n"
                + "open: one of salad, soup, beetle.\n"
                + "main: some of meat, rice, potato, lettuce\n"
                + "desert: one of iceCream, chocolate, appleSauce\n"
                + "todo: nextThing.";
        
        DataDefinitionParser ddfp = new DataDefinitionParser();
        TagType parseTagDefinitions = ddfp.parseTagDefinitions(source, "unitTestExample");
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of format method, of class StringMapFormat.
     */
    @Test
    public void testFormat() {
        System.out.println("format");
        CompoundValue value = null;
        StringMapFormat instance = new StringMapFormat();
        Map<String, String> expResult = null;
        Map<String, String> result = instance.format(value);
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
