/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.model.types.TypeHelper;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    
    TagType dataTagsType;
    
    @Before
    public void setUp() throws DataTagsParseException {
        String source = "DataTags: color, meal, styles, nextThing.\n"
                + "TODO: nextThing.\n"
                + "color: one of red, green, blue, yellow.\n"
                + "styles: some of HipHip, Jazz, Blues, RockAndRoll.\n"
                + "meal: open, main, desert.\n"
                + "open: one of salad, soup, beetle.\n"
                + "main: some of meat, rice, potato, lettuce.\n"
                + "desert: one of iceCream, chocolate, appleSauce.\n"
                ;
        
        DataDefinitionParser ddfp = new DataDefinitionParser();
        dataTagsType = ddfp.parseTagDefinitions(source, "unitTestExample");
    }
    
    /**
     * Test of format method, of class StringMapFormat.
     */
    @Test
    public void testFormat() {
        CompoundValue val = ((CompoundType)dataTagsType).make();
        val.set( TypeHelper.safeGet(dataTagsType, "color", "red") );
        AggregateValue stylesVal = (AggregateValue) TypeHelper.safeGet(dataTagsType, "styles", "Jazz");
        AggregateType stylesType = stylesVal.getType();
        stylesVal.add( stylesType.getItemType().make("Blues", null) );
        stylesVal.add( stylesType.getItemType().make("RockAndRoll", null) );
        
        val.set( stylesVal );
        StringMapFormat sut = new StringMapFormat();
        
        Map<String, String> result = sut.format(val);
        
        for ( Map.Entry<String, String> ent : result.entrySet() ) {
            System.out.println( String.format("%s -> %s", ent.getKey(), ent.getValue()));
        }
    }
    
}
