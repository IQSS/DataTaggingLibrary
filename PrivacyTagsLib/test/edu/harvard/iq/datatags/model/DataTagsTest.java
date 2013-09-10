/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.tags.EncryptionType;
import edu.harvard.iq.datatags.tags.ApprovalType;
import edu.harvard.iq.datatags.tags.DataUseAgreement;
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
		assertTrue( new DataTags(ApprovalType.None, 
									AuthenticationType.None,
									DataUseAgreement.Sign, 
									EncryptionType.DoubleEncryption, 
									EncryptionType.DoubleEncryption).isComplete() );
		assertFalse( new DataTags(ApprovalType.None, 
									null,
									DataUseAgreement.Sign, 
									EncryptionType.DoubleEncryption, 
									EncryptionType.DoubleEncryption).isComplete() );
	}

	/**
	 * Test of composeWith method, of class DataTags.
	 */
	@Test
	public void testComposeWith() {
		DataTags a = new DataTags(null, 
				null, 
				DataUseAgreement.Sign, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		DataTags b = new DataTags(null, 
				AuthenticationType.Email_or_OAuth, 
				DataUseAgreement.None, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		DataTags composed = a.composeWith(b);
		
		// null X null -> null 
		assertNull( composed.getApprovalType() );
		// null X a -> a
		assertEquals( AuthenticationType.Email_or_OAuth, composed.getAuthenticationType() );
		// a X b -> a
		assertEquals( DataUseAgreement.Sign, composed.getDataUseAgreement() );
	}

	/**
	 * Test of stricterThan method, of class DataTags.
	 */
	@Test
	public void testStricterThan() {
		DataTags a = new DataTags(null, 
				AuthenticationType.Password, 
				DataUseAgreement.Sign, 
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
	 * Test of stricterOrEqualTo method, of class DataTags.
	 */
	@Test
	public void testStricterOrEqualTo() {
		DataTags a = new DataTags(null, 
				AuthenticationType.Password, 
				DataUseAgreement.Sign, 
				EncryptionType.DoubleEncryption, 
				EncryptionType.DoubleEncryption);
		
		assertTrue( a.stricterOrEqualTo(a) );
	}
	
}
