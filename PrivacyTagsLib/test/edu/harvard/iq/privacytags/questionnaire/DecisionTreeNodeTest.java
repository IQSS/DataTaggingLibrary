/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.privacytags.questionnaire;

import edu.harvard.iq.privacytags.model.ApprovalType;
import edu.harvard.iq.privacytags.model.AuthenticationType;
import edu.harvard.iq.privacytags.model.DataUseAgreement;
import edu.harvard.iq.privacytags.model.EncryptionType;
import edu.harvard.iq.privacytags.model.PrivacyTagSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author michael
 */
public class DecisionTreeNodeTest {

	/**
	 * Test of getAbsoluteAssumption method, of class DecisionTreeNode.
	 */
	@Test
	public void testGetAbsoluteAssumption() {
		// Set up a basic questionnaire
		int id = 0;
		DecisionTreeNode root = new DecisionTreeNode( Integer.toString(++id) );
		
		DecisionTreeNode nd = new DecisionTreeNode( Integer.toString(++id) );
		nd.setBaseAssumption( new PrivacyTagSet(ApprovalType.None, null, null, null, null) );
		root.setNodeFor(Answer.YES, nd);
		
		DecisionTreeNode ndSub = new DecisionTreeNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new PrivacyTagSet(null, AuthenticationType.Password, null, null, null) );
		nd.setNodeFor(Answer.NO, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionTreeNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new PrivacyTagSet(null, null, DataUseAgreement.Required, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionTreeNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new PrivacyTagSet(ApprovalType.Signed, null, null, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		
		PrivacyTagSet expected = new PrivacyTagSet(ApprovalType.Signed, 
				AuthenticationType.Password, DataUseAgreement.Required, null, null);
		PrivacyTagSet result = ndSub.getAbsoluteAssumption();
		
		assertEquals( expected, result );
	}
	
}
