package edu.harvard.iq.datatags.model.types;

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
	 * Test of registerValue method, of class AtomicType.
	 */
	@Test
	public void testMake() {
		AtomicType instance = new AtomicType("Test", null);
		
		assertTrue( instance.values().isEmpty() );
		
		AtomicValue v1 = instance.registerValue("1", null);
		AtomicValue v2 = instance.registerValue("2", null);
		AtomicValue v3 = instance.registerValue("3", null);
		
		List<AtomicValue> vals = Arrays.asList(v3,v1,v2);
		Collections.sort(vals);
		
		assertEquals( "Values should be ordered in increasing order", Arrays.asList(v1,v2,v3),  vals );
		
		assertEquals( new TreeSet<>(vals), instance.values() );
	}
	
}
