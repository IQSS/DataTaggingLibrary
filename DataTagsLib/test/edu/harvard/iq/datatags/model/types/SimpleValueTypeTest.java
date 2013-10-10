package edu.harvard.iq.datatags.model.types;

import edu.harvard.iq.datatags.model.values.SimpleValue;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class SimpleValueTypeTest {
	
	public SimpleValueTypeTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of make method, of class SimpleValueType.
	 */
	@Test
	public void testMake() {
		SimpleValueType instance = new SimpleValueType("Test", null);
		
		assertTrue( instance.values().isEmpty() );
		
		SimpleValue v1 = instance.make("1", null);
		SimpleValue v2 = instance.make("2", null);
		SimpleValue v3 = instance.make("3", null);
		
		List<SimpleValue> vals = Arrays.asList(v3,v1,v2);
		Collections.sort(vals);
		
		assertEquals( "Values should be ordered in increasing order", Arrays.asList(v1,v2,v3),  vals );
		
		assertEquals( new TreeSet<>(vals), instance.values() );
	}
	
}
