/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.tags.EncryptionType;
import edu.harvard.iq.datatags.tags.DuaAgreementMethod;
import edu.harvard.iq.datatags.tags.AuthenticationType;
import edu.harvard.iq.datatags.tags.DataTags;
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
public class DataTagsTest {
	
	public DataTagsTest() {
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
	 * Test of isComplete method, of class DataTags.
	 */
	@Test
	public void testIsComplete() {
		assertTrue( new DataTags( DuaAgreementMethod.Sign, 
								  AuthenticationType.None,
								  EncryptionType.DoubleEncryption, 
								  EncryptionType.DoubleEncryption).isComplete() );
		assertFalse( new DataTags( DuaAgreementMethod.Sign, 
									null,
									EncryptionType.DoubleEncryption, 
									EncryptionType.DoubleEncryption).isComplete() );
	}

	/**
	 * Test of composeWith method, of class DataTags.
	 */
	@Test
	public void testComposeWith() {
		DataTags a = new DataTags( 
				DuaAgreementMethod.Sign, 
				null, 
				EncryptionType.DoubleEncryption, 
				null);
		DataTags b = new DataTags(
				DuaAgreementMethod.None, 
				AuthenticationType.Email_or_OAuth, 
				EncryptionType.DoubleEncryption, 
				null);
		DataTags composed = a.composeWith(b);
		
		// null X null -> null 
		assertNull( composed.getStorageEncryptionType() );
		// null X a -> a
		assertEquals( AuthenticationType.Email_or_OAuth, composed.getAuthenticationType() );
		// a X b -> a
		assertEquals( DuaAgreementMethod.Sign, composed.getDuaAgreementMethod() );
	}

	/**
	 * Test of stricterThan method, of class DataTags.
	 */
	@Test
	public void testStricterThan() {
		DataTags a = new DataTags( 
				null, 
				AuthenticationType.Password, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		
		assertFalse( a.stricterThan(a) );
		
		assertFalse( "Can't compare when there are different non-null fields",
				a.stricterThan(a.makeCopy().setStorageEncryptionType(null)));
		
		assertFalse( "Can't compare when there are different non-null fields",
				a.stricterThan(a.makeCopy().setDuaAgreementMethod(DuaAgreementMethod.Sign)));
		
		assertFalse( "Copy is MORE strict",
				a.stricterThan(a.makeCopy().setAuthenticationType(AuthenticationType.TwoFactor)));
		
		assertTrue( "Copy is LESS strict",
				a.stricterThan(a.makeCopy().setAuthenticationType(AuthenticationType.None)));
	}

	/**
	 * Test of stricterOrEqualTo method, of class DataTags.
	 */
	@Test
	public void testStricterOrEqualTo() {
		DataTags a = new DataTags( 
				DuaAgreementMethod.Sign, 
				AuthenticationType.Password, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		
		assertTrue( a.stricterOrEqualTo(a) );
	}
	
}
