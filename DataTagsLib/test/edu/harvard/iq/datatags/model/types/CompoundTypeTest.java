/*
 *  (C) Michael Bar-Sinai
 */

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
    
    CompoundType ctSut;
    AtomicType stA, stB;
    AggregateType aggT;
    AtomicType stSetItem;
    
    Map<AtomicType,List<AtomicValue>> values;
    
    @Before
    public void setUp() {
        stA = new AtomicType("A", null);
        stB = new AtomicType("B", null);
        stSetItem = new AtomicType("SetItem",null);
        aggT = new AggregateType("SetOfItems", null, stSetItem);
        
        values = new HashMap<>();
        
        for ( AtomicType st : C.list(stA, stB, stSetItem) ) {
            List<AtomicValue> valueList = new ArrayList<>(4);
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
        
        assertEquals( C.set(stA, stB), val.getTypesWithNonNullValues() );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        assertEquals( values.get(stB).get(0), val.get(stB) );
        
        val.set( stB.make("B-1", null));
        assertEquals( values.get(stB).get(1), val.get(stB) );
        assertEquals( values.get(stA).get(0), val.get(stA) );
        
    }
    
    @Test( expected=IllegalArgumentException.class)
    public void testSimpleSet_fail() {
        CompoundValue val = ctSut.createInstance();
        val.set( new AtomicType("NotThere","").make("banana", null) );
        
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
		AtomicType simple_t1 = new AtomicType("st1", null);
		AtomicType simple_t2 = new AtomicType("st2", null);
		
		AtomicValue v1_t1 = simple_t1.make("1", null);
		AtomicValue v2_t1 = simple_t1.make("2", null);
		
		AtomicValue v1_t2 = simple_t2.make("1", null);
		
        CompoundType ct = new CompoundType("compoundType", null);
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
		AtomicType simple_t = new AtomicType( "t1", null );
		AggregateType agg_t = new AggregateType( "a1", null, simple_t );
		
		AggregateValue agg_1 = agg_t.createInstance();
		AggregateValue agg_2 = agg_t.createInstance();
		
		agg_1.add( simple_t.make("i",null) );
		agg_1.add( simple_t.make("ii",null) );
		agg_2.add( simple_t.make("iii",null) );
		agg_2.add( simple_t.make("iv",null) );
		
		AtomicValue inBoth = simple_t.make("inBoth",null);
		agg_1.add(inBoth);
		agg_2.add(inBoth);
		
        CompoundType ct = new CompoundType("ct",null );
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
		AtomicType simple_t1 = new AtomicType( "OnA", null );
		AtomicType simple_t2 = new AtomicType( "OnB", null );
		AtomicType simple_t3 = new AtomicType( "OnBoth-A bigger", null );
		AtomicType simple_t4 = new AtomicType( "OnBoth-B bigger", null );
		
		List<AtomicType> simpleTypes = Arrays.asList( simple_t1, simple_t2, simple_t3, simple_t4 );
		
		CompoundType compound_t = new CompoundType("compound_t", "The type we test");
		for ( AtomicType st : simpleTypes ) compound_t.addFieldType(st);
		
		for ( AtomicType st : simpleTypes ) {
			int idx=0;
			st.make(st.getName() + (++idx), null);
			st.make(st.getName() + (++idx), null);
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
		
        CompoundType sut = new CompoundType("sut", null);
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
		AtomicType items_t = new AtomicType("Type of items", null);
		
		for ( int i=0; i<3; i++ ) items_t.make("item " + i, null);
		
		AggregateType agg_t = new AggregateType("agg1",null, items_t);
		
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
		
		CompoundType cmp_t = new CompoundType("SUT",null);
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
