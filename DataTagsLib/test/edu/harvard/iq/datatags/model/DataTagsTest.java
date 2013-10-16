package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.types.AggregateType;
import edu.harvard.iq.datatags.model.types.SimpleValueType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.SimpleValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.Collections;
import org.junit.Test;
import static org.junit.Assert.*;
import static edu.harvard.iq.util.CollectionHelper.C;

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
		SimpleValueType tt1 = new SimpleValueType("Test", "info");
		TagValue tt1tv1 = tt1.make( "TestValue1", null);
		TagValue tt1tv2 = tt1.make( "TestValue2", null);
		
		DataTags instance = new DataTags();
		instance.add( tt1tv1 );
		assertSame( tt1tv1, instance.get(tt1) );
		
		instance.add( tt1tv2 );
		assertSame( tt1tv2, instance.get(tt1) );
		
		SimpleValueType tt2 = new SimpleValueType("Test2", null );
		SimpleValue tt2tv1 = tt2.make( "TestValue2Type2", null );
		
		instance.add( tt2tv1 );
		assertSame( tt1tv2, instance.get(tt1) );
		assertSame( tt2tv1, instance.get(tt2) );
	}

	@Test
	public void testRemove() {
		DataTags instance = new DataTags();
		
		SimpleValueType tt = new SimpleValueType("Test2", null );
		SimpleValue tttv1 = tt.make("TestValue2Type2", null);
		
		instance.add( tttv1 );
		assertSame( tttv1, instance.get(tt) );
		
		instance.remove( tt );
		assertNull( instance.get(tt) );
	}
	
	/**
	 * Test of getTypes method, of class DataTags.
	 */
	@Test
	public void testGetTypes() {
		SimpleValueType tt1 = new SimpleValueType("Test", "info");
		SimpleValueType tt2 = new SimpleValueType("Test2", "info");
		
		TagValue tt1tv1 = tt1.make( "TestValue1", null );
		TagValue tt1tv2 = tt1.make( "TestValue2", null );
		TagValue tt2tv1 = tt2.make( "TestValue1", null );
		
		DataTags instance = new DataTags();
		
		instance.add( tt1tv1 );
		assertEquals( Collections.singleton(tt1), instance.getTypes());
		instance.add( tt1tv2 );
		assertEquals( Collections.singleton(tt1), instance.getTypes());
		instance.add( tt2tv1 );
		assertEquals( C.set(tt1, tt2), instance.getTypes());
	}

	/**
	 * Test of makeCopy method, of class DataTags.
	 */
	@Test
	public void testMakeCopy() {
		SimpleValueType simple_t = new SimpleValueType("svt", null);
		SimpleValueType aggregated_t = new SimpleValueType("agg-ed", null);
		AggregateType aggregator_t = new AggregateType("agg-er", null, aggregated_t);
		
		SimpleValue sVal = simple_t.make("sval", null);
		AggregateValue aVal = aggregator_t.make("aval", null);
		
		aVal.add( aggregated_t.make("agg1", null) );
		aVal.add( aggregated_t.make("agg2", null) );
		aVal.add( aggregated_t.make("agg3", null) );
		
		DataTags orig = new DataTags();
		orig.add( aVal );
		orig.add( sVal );
		
		DataTags copy = orig.makeCopy();
		
		assertNotSame( "Result of copy should be a different instance", orig, copy);
		assertNotSame( "Aggregated values should return a copy", orig.get(aggregator_t), copy.get(aggregator_t) );
		assertSame( "Simple values should be shared", orig.get(simple_t), copy.get(simple_t) );
		
	}

	@Test
	public void testComposeWith_simple() {
		SimpleValueType simple_t1 = new SimpleValueType("st1", null);
		SimpleValueType simple_t2 = new SimpleValueType("st2", null);
		
		SimpleValue v1_t1 = simple_t1.make("1", null);
		SimpleValue v2_t1 = simple_t1.make("2", null);
		
		SimpleValue v1_t2 = simple_t2.make("1", null);
		
		DataTags a = new DataTags();
		DataTags b = new DataTags();
		
		a.add( v1_t1 );
		
		b.add( v2_t1 );
		b.add( v1_t2 );
		
		DataTags expected = new DataTags();
		expected.add( v2_t1 ); // as v2_t1.ordinal > v1_t1.ordinal
		expected.add( v1_t2 );
		
		assertEquals( expected, a.composeWith(b) );
		
		a.add( v2_t1 );
		b.add( v1_t1 );

		assertEquals( expected, a.composeWith(b) );
		
	}

	@Test
	public void testComposeWith_aggregate() {
		SimpleValueType simple_t = new SimpleValueType( "t1", null );
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
		dt1.add( agg_1 );
		DataTags dt2 = new DataTags();
		dt2.add( agg_2 );
		
		DataTags actual = dt1.composeWith(dt2);
		DataTags expected = new DataTags();
		AggregateValue expectedAgg_t = agg_t.make("expected", null);
		expectedAgg_t.add( simple_t.values() );
		expected.add( expectedAgg_t );
		
		assertEquals( expected, actual );
	}
}
