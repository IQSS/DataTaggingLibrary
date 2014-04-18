/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class CompoundTypeTest {
    
    public CompoundTypeTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    CompoundType ctSut;
    SimpleType stA, stB;
    AggregateType aggT;
    SimpleType stSetItem;
    
    Map<SimpleType,List<SimpleValue>> values;
    
    @Before
    public void setUp() {
        stA = new SimpleType("A", null);
        stB = new SimpleType("B", null);
        stSetItem = new SimpleType("SetItem",null);
        aggT = new AggregateType("SetOfItems", null, stSetItem);
        
        values = new HashMap<>();
        
        for ( SimpleType st : C.list(stA, stB, stSetItem) ) {
            List<SimpleValue> valueList = new ArrayList<>(4);
            for ( int i=0; i<4; i++ ) {
                valueList.add(st.make( st.getName() + "-" +i, null));
            }
            values.put(st, valueList);
        }
        
        ctSut = new CompoundType("SUT", "The type we test");
        
        ctSut.addFieldType(stA);
        ctSut.addFieldType(stB);
        ctSut.addFieldType(aggT);
    }
    
    @Test
    public void testSimpleSet() {
        CompoundValue val = ctSut.createInstance();
        val.set( stA.make("A-0", null) );
        val.set( stB.make("B-0", null));
        
        assertEquals( C.set(stA, stB), val.getSetFieldTypes() );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        assertEquals( values.get(stB).get(0), val.get(stB) );
        
        val.set( stB.make("B-1", null));
        assertEquals( values.get(stB).get(1), val.get(stB) );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        
    }
    
    @Test( expected=IllegalArgumentException.class)
    public void testSimpleSet_fail() {
        CompoundValue val = ctSut.createInstance();
        val.set( new SimpleType("NotThere","").make("banana", null) );
        
    }
    
    
    @Test
    public void testAggregateSet() {
        CompoundValue val = ctSut.createInstance();
        
        AggregateValue aggValOne = aggT.make();
        List<SimpleValue> itemInstances = values.get(stSetItem);
        
        aggValOne.add( itemInstances.get(0) );
        aggValOne.add( itemInstances.get(1) );
        aggValOne.add( itemInstances.get(2) );
        
        val.set(aggValOne);
        assertEquals( C.set(itemInstances.get(0), itemInstances.get(1), itemInstances.get(2)),
                      ((AggregateValue)val.get(aggT)).getValues() );
        
        AggregateValue aggValTwo = aggT.make();
        aggValTwo.add( itemInstances.get(3) );

        val.set(aggValTwo);
        assertEquals( C.set(itemInstances.get(3)),
                      ((AggregateValue)val.get(aggT)).getValues() );
        
    }
}
