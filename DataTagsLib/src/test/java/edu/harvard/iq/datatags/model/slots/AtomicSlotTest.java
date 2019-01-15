package edu.harvard.iq.datatags.model.slots;

import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.AbstractValue.CompareResult;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
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
public class AtomicSlotTest {
	
	/**
	 * Test of registerValue method, of class AtomicType.
	 */
	@Test
	public void testMake() {
		AtomicSlot instance = new AtomicSlot("Test", null);
		
		assertTrue( instance.values().isEmpty() );
		
		AtomicValue v1 = instance.registerValue("1", null);
		AtomicValue v2 = instance.registerValue("2", null);
		AtomicValue v3 = instance.registerValue("3", null);
		
		List<AtomicValue> vals = Arrays.asList(v3,v1,v2);
		Collections.sort(vals);
		
		assertEquals( "Values should be ordered in increasing order", Arrays.asList(v1,v2,v3),  vals );
		
		assertEquals( new TreeSet<>(vals), instance.values() );
	}

    @Test
    public void testCompare() {
        AtomicSlot instance = new AtomicSlot("Test", null);
		AtomicValue v1 = instance.registerValue("1", null);
		AtomicValue v2 = instance.registerValue("2", null);
		AtomicValue v3 = instance.registerValue("3", null);
        
        AtomicSlot otherSlot = new AtomicSlot("OtherAtomic", null);
        
        assertEquals( CompareResult.Smaller, v2.compare(v3) );
        assertEquals( CompareResult.Same, v2.compare(v2) );
        assertEquals( CompareResult.Bigger, v2.compare(v1) );
        assertEquals( CompareResult.Incomparable, v2.compare(otherSlot.registerValue("X", null)) );
    }
    
    /**
	 * Test of registerValue method, of class AtomicType.
	 */
	@Test( expected=IllegalArgumentException.class )
	public void valueOf() {
		AtomicSlot instance = new AtomicSlot("Test", null);
		
		assertTrue( instance.values().isEmpty() );
		
		AtomicValue v1 = instance.registerValue("1", null);
		AtomicValue v2 = instance.registerValue("2", null);
		
		assertEquals( "value instances shouldbe reused", v1, instance.valueOf("1") );
		assertEquals( "value instances shouldbe reused", v2, instance.valueOf("2") );
        instance.valueOf("a nonexistant value");
	}

}
