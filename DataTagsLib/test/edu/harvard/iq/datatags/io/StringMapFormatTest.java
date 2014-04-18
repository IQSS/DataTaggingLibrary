/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.io.StringMapFormat.TrieNode;
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
        CompoundValue val = makeCompoundValue();
        StringMapFormat sut = new StringMapFormat();
        
        Map<String, String> result = sut.format(val);
        
        for ( Map.Entry<String, String> ent : result.entrySet() ) {
            System.out.println( String.format("%s -> %s", ent.getKey(), ent.getValue()));
        }
        
        TrieNode root = sut.makeTrie(result);
        printTrie( root, 0 );
    }
    
    
    @Test
    public void testPrintTrie() {
        System.out.println("PrintTrie");
        TrieNode root = new TrieNode();
        root.put("simple", new TrieNode() );
        TrieNode sub1 = new TrieNode();
        root.put("parent", sub1);
        sub1.put( "subsub", new TrieNode() );
        sub1.put( "subsub2", new TrieNode() );
        sub1.put( "subsub3", new TrieNode() );
        sub1.get( "subsub3" ).put( "subsubsub", new TrieNode() );
        
        printTrie(root, 0);
        
    }
    
    
    private void printTrie( TrieNode n, int level ) {
        StringBuilder sb = new StringBuilder();
        for ( int i=0; i<level; i++ ) {
            sb.append("-");
        }
        for ( String childName : n.childs.keySet() ) {
            TrieNode c = n.childs.get(childName);
            if ( c.isValue() ) {
                System.out.println( sb.toString() + childName + " (v)");
            } else {
                System.out.println( sb.toString() + childName );
                printTrie( c, level+1 );
            }
        }
    }
    

    private CompoundValue makeCompoundValue() {
        CompoundValue val = ((CompoundType)dataTagsType).createInstance();
        val.set( TypeHelper.safeGet(dataTagsType, "color", "red") );
        AggregateValue stylesVal = (AggregateValue) TypeHelper.safeGet(dataTagsType, "styles", "Jazz");
        AggregateType stylesType = stylesVal.getType();
        stylesVal.add( stylesType.getItemType().make("Blues", null) );
        stylesVal.add( stylesType.getItemType().make("RockAndRoll", null) );
        CompoundType mealType = (CompoundType) val.getType().getTypeNamed("meal");
        CompoundValue meal = mealType.createInstance();
        meal.set( TypeHelper.safeGet(mealType, "open", "salad") );
        meal.set( TypeHelper.safeGet(mealType, "main", "meat") );
        meal.set( TypeHelper.safeGet(mealType, "desert", "appleSauce") );
        val.set( meal );
        val.set( stylesVal );
        return val;
    }
    
}
