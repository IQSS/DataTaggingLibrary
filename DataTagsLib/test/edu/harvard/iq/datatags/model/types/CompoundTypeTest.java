/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.Arrays;
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
        val.set( new SimpleType("NotThere","").make("banana", null) );
        
    }
    
    
    @Test
    public void testAggregateSet() {
        CompoundValue val = ctSut.createInstance();
        
        AggregateValue aggValOne = aggT.createInstance();
        List<SimpleValue> itemInstances = values.get(stSetItem);
        
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
		SimpleType simple_t1 = new SimpleType("st1", null);
		SimpleType simple_t2 = new SimpleType("st2", null);
		
		SimpleValue v1_t1 = simple_t1.make("1", null);
		SimpleValue v2_t1 = simple_t1.make("2", null);
		
		SimpleValue v1_t2 = simple_t2.make("1", null);
		
        CompoundType ct = new CompoundType("compoundType", null);
        ct.addFieldType( simple_t1 );
        ct.addFieldType( simple_t2 );
		CompoundValue a = ct.make("a",null);
		CompoundValue b = ct.make("b",null);
		
		a.set( v1_t1 );
		
		b.set( v2_t1 );
		b.set( v1_t2 );
		
		CompoundValue expected = ct.make("expected",null);
		expected.set( v2_t1 ); // as v2_t1.ordinal > v1_t1.ordinal
		expected.set( v1_t2 );
		
		assertEquals( expected, a.composeWith(b) );
		
		a.set( v2_t1 );
		b.set( v1_t1 );

		assertEquals( expected, a.composeWith(b) );
		
	}

	@Test
	public void testComposeWith_aggregate() {
		SimpleType simple_t = new SimpleType( "t1", null );
		AggregateType agg_t = new AggregateType( "a1", null, simple_t );
		
		AggregateValue agg_1 = agg_t.make("av1", null);
		AggregateValue agg_2 = agg_t.make("av2", null);
		
		agg_1.add( simple_t.make("i",null) );
		agg_1.add( simple_t.make("ii",null) );
		agg_2.add( simple_t.make("iii",null) );
		agg_2.add( simple_t.make("iv",null) );
		
		SimpleValue inBoth = simple_t.make("inBoth",null);
		agg_1.add(inBoth);
		agg_2.add(inBoth);
		
        CompoundType ct = new CompoundType("ct",null );
        ct.addFieldType( simple_t );
        ct.addFieldType( agg_t );
        
		CompoundValue dt1 = ct.make("dt1", null);
		dt1.set( agg_1 );
		CompoundValue dt2 = ct.make("dt2", null);
		dt2.set( agg_2 );
		
		CompoundValue actual = dt1.composeWith(dt2);
		CompoundValue expected = ct.make("exp", null);
		AggregateValue expectedAgg_t = agg_t.make("expected", null);
		expectedAgg_t.add( simple_t.values() );
		expected.set( expectedAgg_t );
		
		assertEquals( expected, actual );
	}
	
	@Test
	public void testComposeWith_compound_single() {
		SimpleType simple_t1 = new SimpleType( "OnA", null );
		SimpleType simple_t2 = new SimpleType( "OnB", null );
		SimpleType simple_t3 = new SimpleType( "OnBoth-A bigger", null );
		SimpleType simple_t4 = new SimpleType( "OnBoth-B bigger", null );
		
		List<SimpleType> simpleTypes = Arrays.asList( simple_t1, simple_t2, simple_t3, simple_t4 );
		
		CompoundType compound_t = new CompoundType("compound_t", "The type we test");
		for ( SimpleType st : simpleTypes ) compound_t.addFieldType(st);
		
		for ( SimpleType st : simpleTypes ) {
			int idx=0;
			st.make(st.getName() + (++idx), null);
			st.make(st.getName() + (++idx), null);
		}
		
		CompoundValue cv1 = compound_t.make("cv1", null);
		cv1.set(C.first(simple_t1.values()) );
		cv1.set(C.list(simple_t3.values()).get(1) );
		cv1.set(C.first(simple_t4.values()) );
		
		CompoundValue cv2 = compound_t.make("cv2", null);
		cv2.set(C.first(simple_t2.values()) );
		cv2.set(C.first(simple_t3.values()) );
		cv2.set(C.list(simple_t4.values()).get(1) );

		CompoundValue cvExpected = compound_t.make("expected", null);
		cvExpected.set(C.first(simple_t1.values()) );
		cvExpected.set(C.first(simple_t2.values()) );
		cvExpected.set(C.list(simple_t3.values()).get(1) );
		cvExpected.set(C.list(simple_t4.values()).get(1) );
		
        CompoundType sut = new CompoundType("sut", null);
        sut.addFieldType(compound_t);
        
		CompoundValue a = sut.make("a", null);
		a.set(cv1);
		CompoundValue b = sut.make("b", null);
		b.set(cv2);
		CompoundValue expected = sut.make("ex", null) ;
		expected.set(cvExpected);
		CompoundValue actual = a.composeWith(b);
		
		assertEquals( expected, actual );
	}
	
	@Test
	public void testComposeWith_compound_aggregate() {
		SimpleType items_t = new SimpleType("Type of items", null);
		
		for ( int i=0; i<3; i++ ) items_t.make("item " + i, null);
		
		AggregateType agg_t = new AggregateType("agg1",null, items_t);
		
		AggregateValue agg_v1 = agg_t.make("aggV1", null);
		AggregateValue agg_v2 = agg_t.make("aggV2", null);
		
		agg_v1.add( C.list(items_t.values()).get(0) );
		agg_v1.add( C.list(items_t.values()).get(1) );
		
		agg_v2.add( C.list(items_t.values()).get(1) );
		agg_v2.add( C.list(items_t.values()).get(2) );
		
		AggregateValue agg_ex = agg_t.make("expected", null);
		agg_ex.add( C.list(items_t.values()).get(0) );
		agg_ex.add( C.list(items_t.values()).get(1) );
		agg_ex.add( C.list(items_t.values()).get(2) );
		
		CompoundType cmp_t = new CompoundType("SUT",null);
		cmp_t.addFieldType(agg_t);
		
		CompoundValue cv1 = cmp_t.make("cv1", null);
		cv1.set(agg_v1);
		CompoundValue cv2 = cmp_t.make("cv2", null);
		cv2.set(agg_v2);
		CompoundValue cvExpected = cmp_t.make("cv-expected", null);
		cvExpected.set(agg_ex);
		
		assertEquals( cvExpected, cv1.composeWith(cv2) );
		
		
	}
}
