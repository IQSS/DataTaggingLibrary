package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue.CompareResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import static org.junit.Assert.*;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import org.junit.Before;
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
    
    @Before
    public void setUp() throws Exception {
        String code = "DataTags : consists of Bag, Burrito.\n" +
                      "Bag : one of banana_leaf, paper, plastic.\n" +
                      "Burrito : consists of Wrap, Main, Side.\n" +
                      "Wrap : one of corn, full_grain, wheat.\n" +
                      "Main : one of  chicken, tofu, beef.\n" +
                      "Side : some of rice, corn, guacamole, cream, cheese.";
        lunchType = new TagSpaceParser().parse(code).buildType("DataTags").get();
        bagType = (AtomicSlot) lunchType.getSubSlot("Bag");
        burritoType = (CompoundSlot) lunchType.getSubSlot("Burrito");
        wrapType = (AtomicSlot) burritoType.getSubSlot("Wrap");
        sideType = (AggregateSlot) burritoType.getSubSlot("Side");
        mainType = (AtomicSlot) burritoType.getSubSlot("Main");
    }
    
    @Test
    public void testIsSupersetOf_reflective() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.put( wrapType.valueOf("corn") );
        burrito.put( mainType.valueOf("chicken") );
        val.put( burrito );
        
        
        assertTrue("A value should be a superset of itself", val.isSupersetOf(val));
    }
    
    @Test
    public void testIsSupersetOf_level0() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.put( wrapType.valueOf("corn") );
        burrito.put( mainType.valueOf("chicken") );
        val.put( burrito );
        
        CompoundValue minVal = val.getOwnableInstance();
        
        val.put( bagType.valueOf("banana_leaf") );
        
        assertTrue("val should be a superset of minVal as it has the Wrap field set", val.isSupersetOf(minVal));
        assertFalse("minVal is not a superset if val", minVal.isSupersetOf(val));
        
    }
    
    @Test
    public void testProjection() {
        CompoundValue lunch1 = lunchType.createInstance();
        CompoundValue lunch2 = lunchType.createInstance();
        CompoundValue lunch3 = lunchType.createInstance();
        
        lunch1.put( bagType.valueOf("paper") );
        
        CompoundValue burritoOfL2 = burritoType.createInstance();
        burritoOfL2.put( wrapType.valueOf("corn") );
        burritoOfL2.put( mainType.valueOf("tofu") );
        lunch2.put( burritoOfL2 );
        
        lunch3.put( bagType.valueOf("paper") );
        lunch3.put( burritoOfL2 );

        assertTrue( lunch1.project(lunch2.getNonEmptySubSlots()).isEmpty() );
        assertEquals( lunch1, lunch3.project(lunch1.getNonEmptySubSlots()) );
        assertEquals( lunch2, lunch3.project(lunch2.getNonEmptySubSlots()) );

    }
    
    @Test
    public void testCompare() {
        
        CompoundValue lunchS = lunchType.createInstance();
        CompoundValue lunchB = lunchType.createInstance();
        CompoundValue lunchM1 = lunchType.createInstance();
        CompoundValue lunchM2 = lunchType.createInstance();
        
        // prepare the smallest lunch.
        lunchS.put( bagType.valueOf("banana_leaf") );
        CompoundValue burritoS = burritoType.createInstance();
        burritoS.put( wrapType.values().first() );
        burritoS.put( mainType.values().first() );
        burritoS.put( sideType.createInstance() );
        lunchS.put(burritoS);
        
        // prepare biggest lunch
        lunchB.put( bagType.values().last() );
        CompoundValue burritoB = burritoType.createInstance();
        burritoB.put( wrapType.values().last() );
        burritoB.put( mainType.values().last() );
        burritoB.put( sideType.createInstance() );
        
        final AggregateValue fullSides = sideType.createInstance();
        fullSides.add( sideType.getItemType().values() );
        burritoB.put( fullSides );
        lunchB.put(burritoB);
        
        // Medium lunches
        lunchM1.put( bagType.valueOf("paper") );
        lunchM1.put( burritoB );
        
        lunchM2.put( bagType.valueOf("plastic") );
        lunchM2.put( burritoS );
        
        assertEquals( CompareResult.Same,         lunchM1.compare(lunchM1) );
        assertEquals( CompareResult.Smaller,      lunchS.compare(lunchB)  );
        assertEquals( CompareResult.Bigger,       lunchB.compare(lunchS)  );
        assertEquals( CompareResult.Bigger,       lunchM1.compare(lunchS)  );
        assertEquals( CompareResult.Smaller,      lunchM1.compare(lunchB)  );
        assertEquals( CompareResult.Incomparable, lunchM1.compare(lunchM2) );
        
    }
    
    @Test
    public void testIsSupersetOf_level1_aggregate() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.put( wrapType.valueOf("corn") );
        burrito.put( mainType.valueOf("chicken") );
        val.put( burrito );
        
        CompoundValue minVal = val.getOwnableInstance();
        
        AggregateValue sides = sideType.createInstance();
        sides.add( sideType.getItemType().valueOf("corn") );
        burrito.put( sides );
        
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
        burrito1.put( wrapType.valueOf("corn") );
        burrito1.put( mainType.valueOf("chicken") );
        val1.put( burrito1 );
        val1.put( bagType.valueOf("plastic") );
        
        CompoundValue val2 = lunchType.createInstance();
        CompoundValue burrito2 = burritoType.createInstance();
        burrito2.put( wrapType.valueOf("corn") );
        burrito2.put( mainType.valueOf("chicken") );
        val2.put( burrito2 );
        val2.put( bagType.valueOf("paper") );
        
        // testing changes on level 1
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
        // on to testing internal level (atomic)
        val2.put( val1.get(bagType) );
        burrito2.put( wrapType.valueOf("wheat"));
        
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
        // internal level - aggregate
        burrito2.put( burrito1.get(wrapType) );
        
        AggregateValue sides1 = sideType.createInstance();
        sides1.add( sideType.getItemType().valueOf("cream") );
        burrito1.put( sides1 );
        
        AggregateValue sides2 = sideType.createInstance();
        sides2.add( sideType.getItemType().valueOf("cheese") );
        burrito2.put( sides2 );
        
        assertFalse( val1.isSupersetOf(val2) );
        assertFalse( val2.isSupersetOf(val1) );
        
    }
    
    @Test
    public void testEquals() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        burrito.put( wrapType.valueOf("corn") );
        burrito.put( mainType.valueOf("chicken") );
        val.put( burrito );
        
        CompoundValue otherVal = val.getOwnableInstance();
        
        assertNotSame( val, otherVal );
        assertEquals( otherVal, val );
        
        val.put( bagType.valueOf("paper") );
        
        assertFalse( val.equals(otherVal) );
    }
    
    @Test
    public void testGetValue() {
        CompoundValue val = lunchType.createInstance();
        CompoundValue burrito = burritoType.createInstance();
        AtomicValue aVal = new AtomicValue(0, "chicken", mainType, "");
       
        burrito.put( wrapType.valueOf("corn") );
        burrito.put( mainType.valueOf("chicken") );
        val.put(burrito);
        
        assertEquals(aVal, val.getValue(C.list("Burrito", "Main")));
        assertThrows(IllegalArgumentException.class, () -> val.getValue(C.list("not", "exist")));
    }
}
