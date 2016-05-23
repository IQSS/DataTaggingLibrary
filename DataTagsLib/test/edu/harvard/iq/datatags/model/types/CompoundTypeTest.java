package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.AfterClass;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

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
    
    CompoundSlot ctSut;
    AtomicSlot stA, stB;
    AggregateSlot aggT;
    AtomicSlot stSetItem;
    
    Map<AtomicSlot,List<AtomicValue>> values;
    
    @Before
    public void setUp() {
        stA = new AtomicSlot("A", null);
        stB = new AtomicSlot("B", null);
        stSetItem = new AtomicSlot("SetItem",null);
        aggT = new AggregateSlot("SetOfItems", null, stSetItem);
        
        values = new HashMap<>();
        
        for ( AtomicSlot st : C.list(stA, stB, stSetItem) ) {
            List<AtomicValue> valueList = new ArrayList<>(4);
            for ( int i=0; i<4; i++ ) {
                valueList.add(st.registerValue( st.getName() + "-" +i, null));
            }
            values.put(st, valueList);
        }
        
        ctSut = new CompoundSlot("SUT", "The type we test");
        
        ctSut.addFieldType(stA);
        ctSut.addFieldType(stB);
        ctSut.addFieldType(aggT);
    }
    
    @Test
    public void testSimpleSet() {
        CompoundValue val = ctSut.createInstance();
        val.set( TypeHelper.getCreateValue(stA, "A-0", null) );
        val.set( TypeHelper.getCreateValue(stB, "B-0", null) );
        
        assertEquals( C.set(stA, stB), val.getNonEmptySubSlotTypes() );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        assertEquals( values.get(stB).get(0), val.get(stB) );
        
        val.set( TypeHelper.getCreateValue(stB, "B-1", null) );
        assertEquals( values.get(stB).get(1), val.get(stB) );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        
    }
    
    @Test( expected=IllegalArgumentException.class)
    public void testSimpleSet_fail() {
        CompoundValue val = ctSut.createInstance();
        val.set( new AtomicSlot("NotThere","").registerValue("banana", null) );
        
    }
    
    
    @Test
    public void testAggregateSet() {
        CompoundValue val = ctSut.createInstance();
        
        AggregateValue aggValOne = aggT.createInstance();
        List<AtomicValue> itemInstances = values.get(stSetItem);
        
        aggValOne.add( itemInstances.get(0) );
        aggValOne.add( itemInstances.get(1) );
        aggValOne.add( itemInstances.get(2) );
        
        val.set(aggValOne);
        assertEquals( C.set(itemInstances.get(0), itemInstances.get(1), itemInstances.get(2)),
                      ((AggregateValue)val.get(aggT)).getValues() );
        
        AggregateValue aggValTwo = aggT.createInstance();
        aggValTwo.add( itemInstances.get(3) );

        val.set(aggValTwo);
        assertEquals( C.set(itemInstances.get(3)),
                      ((AggregateValue)val.get(aggT)).getValues() );
        
    }
    
    @Test
	public void testComposeWith_simple() {
		AtomicSlot simple_t1 = new AtomicSlot("st1", null);
		AtomicSlot simple_t2 = new AtomicSlot("st2", null);
		
		AtomicValue v1_t1 = simple_t1.registerValue("1", null);
		AtomicValue v2_t1 = simple_t1.registerValue("2", null);
		
		AtomicValue v1_t2 = simple_t2.registerValue("1", null);
		
        CompoundSlot ct = new CompoundSlot("compoundType", null);
        ct.addFieldType( simple_t1 );
        ct.addFieldType( simple_t2 );
		CompoundValue a = ct.createInstance();
		CompoundValue b = ct.createInstance();
		
		a.set( v1_t1 );
		
		b.set( v2_t1 );
		b.set( v1_t2 );
		
		CompoundValue expected = ct.createInstance();
		expected.set( v2_t1 ); // as v2_t1.ordinal > v1_t1.ordinal
		expected.set( v1_t2 );
		
		assertEquals( expected, a.composeWith(b) );
		
		a.set( v2_t1 );
		b.set( v1_t1 );

		assertEquals( expected, a.composeWith(b) );
		
	}

	@Test
	public void testComposeWith_aggregate() {
		AtomicSlot simple_t = new AtomicSlot( "t1", null );
		AggregateSlot agg_t = new AggregateSlot( "a1", null, simple_t );
		
		AggregateValue agg_1 = agg_t.createInstance();
		AggregateValue agg_2 = agg_t.createInstance();
		
		agg_1.add( simple_t.registerValue("i",null) );
		agg_1.add( simple_t.registerValue("ii",null) );
		agg_2.add( simple_t.registerValue("iii",null) );
		agg_2.add( simple_t.registerValue("iv",null) );
		
		AtomicValue inBoth = simple_t.registerValue("inBoth",null);
		agg_1.add(inBoth);
		agg_2.add(inBoth);
		
        CompoundSlot ct = new CompoundSlot("ct",null );
        ct.addFieldType( simple_t );
        ct.addFieldType( agg_t );
        
		CompoundValue dt1 = ct.createInstance();
		dt1.set( agg_1 );
		CompoundValue dt2 = ct.createInstance();
		dt2.set( agg_2 );
		
		CompoundValue actual = dt1.composeWith(dt2);
		CompoundValue expected = ct.createInstance();
		AggregateValue expectedAgg_t = agg_t.createInstance();
		expectedAgg_t.add( simple_t.values() );
		expected.set( expectedAgg_t );
		
		assertEquals( expected, actual );
	}
	
	@Test
	public void testComposeWith_compound_single() {
		AtomicSlot simple_t1 = new AtomicSlot( "OnA", null );
		AtomicSlot simple_t2 = new AtomicSlot( "OnB", null );
		AtomicSlot simple_t3 = new AtomicSlot( "OnBoth-A bigger", null );
		AtomicSlot simple_t4 = new AtomicSlot( "OnBoth-B bigger", null );
		
		List<AtomicSlot> simpleTypes = Arrays.asList( simple_t1, simple_t2, simple_t3, simple_t4 );
		
		CompoundSlot compound_t = new CompoundSlot("compound_t", "The type we test");
		for ( AtomicSlot st : simpleTypes ) compound_t.addFieldType(st);
		
		for ( AtomicSlot st : simpleTypes ) {
			int idx=0;
			st.registerValue(st.getName() + (++idx), null);
			st.registerValue(st.getName() + (++idx), null);
		}
		
		CompoundValue cv1 = compound_t.createInstance();
		cv1.set(C.first(simple_t1.values()) );
		cv1.set(C.list(simple_t3.values()).get(1) );
		cv1.set(C.first(simple_t4.values()) );
		
		CompoundValue cv2 = compound_t.createInstance();
		cv2.set(C.first(simple_t2.values()) );
		cv2.set(C.first(simple_t3.values()) );
		cv2.set(C.list(simple_t4.values()).get(1) );

		CompoundValue cvExpected = compound_t.createInstance();
		cvExpected.set(C.first(simple_t1.values()) );
		cvExpected.set(C.first(simple_t2.values()) );
		cvExpected.set(C.list(simple_t3.values()).get(1) );
		cvExpected.set(C.list(simple_t4.values()).get(1) );
		
        CompoundSlot sut = new CompoundSlot("sut", null);
        sut.addFieldType(compound_t);
        
		CompoundValue a = sut.createInstance();
		a.set(cv1);
		CompoundValue b = sut.createInstance();
		b.set(cv2);
		CompoundValue expected = sut.createInstance();
		expected.set(cvExpected);
		CompoundValue actual = a.composeWith(b);
		
		assertEquals( expected, actual );
	}
	
	@Test
	public void testComposeWith_compound_aggregate() {
		AtomicSlot items_t = new AtomicSlot("Type of items", null);
		
		for ( int i=0; i<3; i++ ) items_t.registerValue("item " + i, null);
		
		AggregateSlot agg_t = new AggregateSlot("agg1",null, items_t);
		
		AggregateValue agg_v1 = agg_t.createInstance();
		AggregateValue agg_v2 = agg_t.createInstance();
		
		agg_v1.add( C.list(items_t.values()).get(0) );
		agg_v1.add( C.list(items_t.values()).get(1) );
		
		agg_v2.add( C.list(items_t.values()).get(1) );
		agg_v2.add( C.list(items_t.values()).get(2) );
		
		AggregateValue agg_ex = agg_t.createInstance();
		agg_ex.add( C.list(items_t.values()).get(0) );
		agg_ex.add( C.list(items_t.values()).get(1) );
		agg_ex.add( C.list(items_t.values()).get(2) );
		
		CompoundSlot cmp_t = new CompoundSlot("SUT",null);
		cmp_t.addFieldType(agg_t);
		
		CompoundValue cv1 = cmp_t.createInstance();
		cv1.set(agg_v1);
		CompoundValue cv2 = cmp_t.createInstance();
		cv2.set(agg_v2);
		CompoundValue cvExpected = cmp_t.createInstance();
		cvExpected.set(agg_ex);
		
		assertEquals( cvExpected, cv1.composeWith(cv2) );
		
		
	}
}
