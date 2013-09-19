/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.questionnaire;

import edu.harvard.iq.datatags.questionnaire.Answer;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import edu.harvard.iq.datatags.tags.ApprovalType;
import edu.harvard.iq.datatags.tags.AuthenticationType;
import edu.harvard.iq.datatags.tags.DataUseAgreement;
import edu.harvard.iq.datatags.tags.DataTags;
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
		DecisionNode root = new DecisionNode( Integer.toString(++id) );
		
		DecisionNode nd = new DecisionNode( Integer.toString(++id) );
		nd.setBaseAssumption( new DataTags(ApprovalType.None, null, null, null, null) );
		root.setNodeFor(Answer.YES, nd);
		
		DecisionNode ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags(null, AuthenticationType.Password, null, null, null) );
		nd.setNodeFor(Answer.NO, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags(null, null, DataUseAgreement.Sign, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags(ApprovalType.Signed, null, null, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		
		DataTags expected = new DataTags(ApprovalType.Signed, 
				AuthenticationType.Password, DataUseAgreement.Sign, null, null);
		DataTags result = ndSub.getAbsoluteAssumption();
		
		assertEquals( expected, result );
	}
	
}
