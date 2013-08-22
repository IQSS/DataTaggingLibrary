/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.privacytags.model;

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
public class PrivacyTagSetTest {
	
	public PrivacyTagSetTest() {
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
	 * Test of isComplete method, of class PrivacyTagSet.
	 */
	@Test
	public void testIsComplete() {
		assertTrue( new PrivacyTagSet(ApprovalType.None, 
									AuthenticationType.None,
									DataUseAgreement.Required, 
									EncryptionType.DoubleEncryption, 
									EncryptionType.DoubleEncryption).isComplete() );
		assertFalse( new PrivacyTagSet(ApprovalType.None, 
									null,
									DataUseAgreement.Required, 
									EncryptionType.DoubleEncryption, 
									EncryptionType.DoubleEncryption).isComplete() );
	}

	/**
	 * Test of composeWith method, of class PrivacyTagSet.
	 */
	@Test
	public void testComposeWith() {
		PrivacyTagSet a = new PrivacyTagSet(null, 
				null, 
				DataUseAgreement.Required, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		PrivacyTagSet b = new PrivacyTagSet(null, 
				AuthenticationType.Email_or_OAuth, 
				DataUseAgreement.None, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		PrivacyTagSet composed = a.composeWith(b);
		
		// null X null -> null 
		assertNull( composed.getApprovalType() );
		// null X a -> a
		assertEquals( AuthenticationType.Email_or_OAuth, composed.getAuthenticationType() );
		// a X b -> a
		assertEquals( DataUseAgreement.Required, composed.getDataUseAgreement() );
	}

	/**
	 * Test of stricterThan method, of class PrivacyTagSet.
	 */
	@Test
	public void testStricterThan() {
		PrivacyTagSet a = new PrivacyTagSet(null, 
				AuthenticationType.Password, 
				DataUseAgreement.Required, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		
		assertFalse( a.stricterThan(a) );
		
		assertFalse( "Can't compare when there are different non-null fields",
				a.stricterThan(a.makeCopy().setStorageEncryptionType(null)));
		
		assertFalse( "Can't compare when there are different non-null fields",
				a.stricterThan(a.makeCopy().setApprovalType(ApprovalType.None)));
		
		assertFalse( "Copy is MORE strict",
				a.stricterThan(a.makeCopy().setAuthenticationType(AuthenticationType.TwoFactor)));
		
		assertTrue( "Copy is LESS strict",
				a.stricterThan(a.makeCopy().setAuthenticationType(AuthenticationType.None)));
	}

	/**
	 * Test of stricterOrEqualTo method, of class PrivacyTagSet.
	 */
	@Test
	public void testStricterOrEqualTo() {
		PrivacyTagSet a = new PrivacyTagSet(null, 
				AuthenticationType.Password, 
				DataUseAgreement.Required, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		
		assertTrue( a.stricterOrEqualTo(a) );
	}
	
}
