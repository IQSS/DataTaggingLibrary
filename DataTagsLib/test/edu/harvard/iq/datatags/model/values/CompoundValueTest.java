package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.types.AggregateSlot;
import edu.harvard.iq.datatags.model.types.AtomicSlot;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import org.junit.After;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class CompoundValueTest {
    
    CompoundSlot lunchType;
    CompoundSlot burritoType;
    AtomicSlot bagType;
    AtomicSlot wrapType;
    AtomicSlot mainType;
    AggregateSlot sideType;
    
    public CompoundValueTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() throws Exception {
        String code = "DataTags : consists of Bag , Burrito.\n" +
                      "Bag : one of banana_leaf, paper, plastic.\n" +
                      "Burrito : consists of Wrap , Main , Side.\n" +
                      "Wrap : one of corn , full_grain , wheat.\n" +
                      "Main : one of  chicken, tofu, beef.\n" +
                      "Side : some of rice , corn , guacamole ,cream, cheese.";
        lunchType = new TagSpaceParser().parse(code).buildType("DataTags").get();
        bagType = (AtomicSlot) lunchType.getTypeNamed("Bag");
        burritoType = (CompoundSlot) lunchType.getTypeNamed("Burrito");
        wrapType = (AtomicSlot) burritoType.getTypeNamed("Wrap");
        sideType = (AggregateSlot) burritoType.getTypeNamed("Side");
        mainType = (AtomicSlot) burritoType.getTypeNamed("Main");
    }
    
    @After
    public void tearDown() {
    }

    @Test
    public void testIsSupersetOf_reflective() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.set( wrapType.valueOf("corn") );
        burrito.set( mainType.valueOf("chicken") );
        val.set( burrito );
        
        
        assertTrue("A value should be a supertype of itself", val.isSupersetOf(val));
    }
    
    @Test
    public void testIsSupersetOf_level0() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.set( wrapType.valueOf("corn") );
        burrito.set( mainType.valueOf("chicken") );
        val.set( burrito );
        
        CompoundValue minVal = val.getOwnableInstance();
        
        val.set( bagType.valueOf("banana_leaf") );
        
        assertTrue("val should be a superset of minVal as it has the Wrap field set", val.isSupersetOf(minVal));
        assertFalse("minVal is not a superset if val", minVal.isSupersetOf(val));
        
    }

    @Test
    public void testIsSupersetOf_level1_aggregate() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.set( wrapType.valueOf("corn") );
        burrito.set( mainType.valueOf("chicken") );
        val.set( burrito );
        
        CompoundValue minVal = val.getOwnableInstance();
        
        AggregateValue sides = sideType.createInstance();
        sides.add( sideType.getItemType().valueOf("corn") );
        burrito.set( sides );
        
        assertTrue("val should be a superset of minVal as its burrito has the sides field set",
                    val.isSupersetOf(minVal));
        assertFalse("minVal is not a superset if val", minVal.isSupersetOf(val));
        
        minVal = val.getOwnableInstance();
        
        sides.add( sideType.getItemType().valueOf("cream") );
        
        assertTrue("val should be a superset of minVal as its burrito sides also contains cream",
                    val.isSupersetOf(minVal));
        assertFalse("minVal is not a superset if val", minVal.isSupersetOf(val));
        
        
    }
    
    @Test
    public void testIsSupersetOf_differentValues() {
        CompoundValue val1 = lunchType.createInstance();
        CompoundValue burrito1 = burritoType.createInstance();
        burrito1.set( wrapType.valueOf("corn") );
        burrito1.set( mainType.valueOf("chicken") );
        val1.set( burrito1 );
        val1.set( bagType.valueOf("plastic") );
        
        CompoundValue val2 = lunchType.createInstance();
        CompoundValue burrito2 = burritoType.createInstance();
        burrito2.set( wrapType.valueOf("corn") );
        burrito2.set( mainType.valueOf("chicken") );
        val2.set( burrito2 );
        val2.set( bagType.valueOf("paper") );
        
        // testing changes on level 1
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
        // on to testing internal level (atomic)
        val2.set( val1.get(bagType) );
        burrito2.set( wrapType.valueOf("wheat"));
        
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
        // internal level - aggregate
        burrito2.set( burrito1.get(wrapType) );
        
        AggregateValue sides1 = sideType.createInstance();
        sides1.add( sideType.getItemType().valueOf("cream") );
        burrito1.set( sides1 );
        
        AggregateValue sides2 = sideType.createInstance();
        sides2.add( sideType.getItemType().valueOf("cheese") );
        burrito2.set( sides2 );
        
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
    }
    
    @Test
    public void testEquals() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.set( wrapType.valueOf("corn") );
        burrito.set( mainType.valueOf("chicken") );
        val.set( burrito );
        
        CompoundValue otherVal = val.getOwnableInstance();
        
        assertNotSame( val, otherVal );
        assertEquals( otherVal, val );
        
        val.set( bagType.valueOf("paper") );
        
        assertFalse( val.equals(otherVal) );
    }
}
