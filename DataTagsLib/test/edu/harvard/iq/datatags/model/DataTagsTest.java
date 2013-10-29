package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.SimpleType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author michael
 */
public class DataTagsTest {
	
	/**
	 * Test of set method, of class DataTags.
	 */
	@Test
	public void testSet() {
		SimpleType tt1 = new SimpleType("Test", "info");
		TagValue tt1tv1 = tt1.make( "TestValue1", null);
		TagValue tt1tv2 = tt1.make( "TestValue2", null);
		
		DataTags instance = new DataTags();
		instance.set( tt1tv1 );
		assertSame( tt1tv1, instance.get(tt1) );
		
		instance.set( tt1tv2 );
		assertSame( tt1tv2, instance.get(tt1) );
		
		SimpleType tt2 = new SimpleType("Test2", null );
		SimpleValue tt2tv1 = tt2.make( "TestValue2Type2", null );
		
		instance.set( tt2tv1 );
		assertSame( tt1tv2, instance.get(tt1) );
		assertSame( tt2tv1, instance.get(tt2) );
	}

	@Test
	public void testRemove() {
		DataTags instance = new DataTags();
		
		SimpleType tt = new SimpleType("Test2", null );
		SimpleValue tttv1 = tt.make("TestValue2Type2", null);
		
		instance.set( tttv1 );
		assertSame( tttv1, instance.get(tt) );
		
		instance.clear( tt );
		assertNull( instance.get(tt) );
	}
	
	/**
	 * Test of getTypes method, of class DataTags.
	 */
	@Test
	public void testGetTypes() {
		SimpleType tt1 = new SimpleType("Test", "info");
		SimpleType tt2 = new SimpleType("Test2", "info");
		
		TagValue tt1tv1 = tt1.make( "TestValue1", null );
		TagValue tt1tv2 = tt1.make( "TestValue2", null );
		TagValue tt2tv1 = tt2.make( "TestValue1", null );
		
		DataTags instance = new DataTags();
		
		instance.set( tt1tv1 );
		assertEquals( Collections.singleton(tt1), instance.getSetFieldTypes());
		instance.set( tt1tv2 );
		assertEquals( Collections.singleton(tt1), instance.getSetFieldTypes());
		instance.set( tt2tv1 );
		assertEquals( C.set(tt1, tt2), instance.getSetFieldTypes());
	}

	/**
	 * Test of makeCopy method, of class DataTags.
	 */
	@Test
	public void testMakeCopy() {
		SimpleType simple_t = new SimpleType("svt", null);
		SimpleType aggregated_t = new SimpleType("agg-ed", null);
		AggregateType aggregator_t = new AggregateType("agg-er", null, aggregated_t);
		
		SimpleValue sVal = simple_t.make("sval", null);
		AggregateValue aVal = aggregator_t.make("aval", null);
		
		aVal.add( aggregated_t.make("agg1", null) );
		aVal.add( aggregated_t.make("agg2", null) );
		aVal.add( aggregated_t.make("agg3", null) );
		
		DataTags orig = new DataTags();
		orig.set( aVal );
		orig.set( sVal );
		
		DataTags copy = orig.getOwnableInstance();
		
		assertNotSame( "Result of copy should be a different instance", orig, copy);
		assertNotSame( "Aggregated values should return a copy", orig.get(aggregator_t), copy.get(aggregator_t) );
		assertSame( "Simple values should be shared", orig.get(simple_t), copy.get(simple_t) );
		
	}

	@Test
	public void testComposeWith_simple() {
		SimpleType simple_t1 = new SimpleType("st1", null);
		SimpleType simple_t2 = new SimpleType("st2", null);
		
		SimpleValue v1_t1 = simple_t1.make("1", null);
		SimpleValue v2_t1 = simple_t1.make("2", null);
		
		SimpleValue v1_t2 = simple_t2.make("1", null);
		
		DataTags a = new DataTags();
		DataTags b = new DataTags();
		
		a.set( v1_t1 );
		
		b.set( v2_t1 );
		b.set( v1_t2 );
		
		DataTags expected = new DataTags();
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
		
		DataTags dt1 = new DataTags();
		dt1.set( agg_1 );
		DataTags dt2 = new DataTags();
		dt2.set( agg_2 );
		
		DataTags actual = dt1.composeWith(dt2);
		DataTags expected = new DataTags();
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
		
		CompoundType compound_t = new CompoundType("SUT", "The type we test");
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
		
		DataTags a = new DataTags();
		a.set(cv1);
		DataTags b = new DataTags();
		b.set(cv2);
		DataTags expected = new DataTags();
		expected.set(cvExpected);
		DataTags actual = a.composeWith(b);
		
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
		CompoundValue cvE = cmp_t.make("cv-expected", null);
		cvE.set(agg_ex);
		
		DataTags a = new DataTags();
		a.set(cv1);
		DataTags b = new DataTags();
		b.set(cv2);
		DataTags expected = new DataTags();
		expected.set(cvE);
		DataTags actual = a.composeWith(b);
		
		assertEquals( expected, actual );
		
		
	}
}
