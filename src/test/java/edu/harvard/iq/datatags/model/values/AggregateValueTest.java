package edu.harvard.iq.datatags.model.values;

import edu.harvard.iq.datatags.model.slots.AggregateSlot;
import edu.harvard.iq.datatags.model.slots.AtomicSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue.CompareResult;
import static org.junit.Assert.assertEquals;
import org.junit.Test;

/**
 *
 * @author michael
 */
public class AggregateValueTest {
    
    @Test
    public void testCompare() {
        AtomicSlot stSetItem = new AtomicSlot("Items_subs",null);
        stSetItem.registerValue("a", null);
        stSetItem.registerValue("b", null);
        stSetItem.registerValue("c", null);
        AggregateSlot sutSlot = new AggregateSlot("Items", null, stSetItem);
        
        AggregateValue v = sutSlot.createInstance();
        AggregateValue vA = sutSlot.createInstance();
        AggregateValue vB = sutSlot.createInstance();
        AggregateValue vAB = sutSlot.createInstance();
        
        vA.add(stSetItem.valueOf("a"));
        vB.add(stSetItem.valueOf("b"));
        vAB.add(stSetItem.valueOf("a"));
        vAB.add(stSetItem.valueOf("b"));
        
        assertEquals( CompareResult.Same, vA.compare(vA) );
        assertEquals( CompareResult.Incomparable, vA.compare(vB) );
        assertEquals( CompareResult.Bigger, vAB.compare(vB) );
        assertEquals( CompareResult.Bigger, vAB.compare(vA) );
        assertEquals( CompareResult.Bigger, vAB.compare(v) );
        assertEquals( CompareResult.Bigger, vA.compare(v) );
        assertEquals( CompareResult.Smaller, v.compare(vA) );
        assertEquals( CompareResult.Smaller, v.compare(vB) );
        assertEquals( CompareResult.Smaller, v.compare(vAB) );
        assertEquals( CompareResult.Smaller, vA.compare(vAB) );
        
    }
    
}
