package edu.harvard.iq.datatags.io;

import edu.harvard.iq.datatags.io.StringMapFormat.TrieNode;
import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.slots.SlotHelper;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.util.Map;
import static org.junit.Assert.assertEquals;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class StringMapFormatTest {
    
    public StringMapFormatTest() {
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
    
    /**
     * Test of format method, of class StringMapFormat.
     */
    @Test
    public void testFormat() {
        CompoundValue val = makeCompoundValue();
        StringMapFormat sut = new StringMapFormat();
        
        Map<String, String> result = sut.format(val);
        
        result.entrySet().forEach( ent -> 
            System.out.println( String.format("%s -> %s", ent.getKey(), ent.getValue()))
        );
        
        TrieNode root = sut.makeTrie(result);
        printTrie( root, 0 );
    }
    
    @Test
    public void testRoundTrip() {
        CompoundValue expected = makeCompoundValue();
        StringMapFormat sut = new StringMapFormat();
        
        Map<String, String> serialized = sut.format(expected);
        
        CompoundValue result = (CompoundValue) sut.parse(dataTagsType, serialized);
        assertEquals( expected, result );
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
        CompoundValue val = ((CompoundSlot)dataTagsType).createInstance();
        val.put(SlotHelper.safeGet(dataTagsType, "color", "red") );
        AggregateValue stylesVal = (AggregateValue) SlotHelper.safeGet(dataTagsType, "styles", "Jazz");
        AggregateSlot stylesType = stylesVal.getSlot();
        stylesVal.add(SlotHelper.getCreateValue( stylesType.getItemType(), "Blues", "") );
        stylesVal.add(SlotHelper.getCreateValue( stylesType.getItemType(), "RockAndRoll", "") );
        CompoundSlot mealType = (CompoundSlot) val.getSlot().getSubSlot("meal");
        CompoundValue meal = mealType.createInstance();
        meal.put(SlotHelper.safeGet(mealType, "open", "salad") );
        meal.put(SlotHelper.safeGet(mealType, "main", "meat") );
        meal.put(SlotHelper.safeGet(mealType, "desert", "appleSauce") );
        val.put( meal );
        val.put( stylesVal );
        return val;
    }
    
}
