/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.questionnaire;

import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.tags.AuthenticationType;
import edu.harvard.iq.datatags.tags.DuaAgreementMethod;
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
		nd.setBaseAssumption( new DataTags(null, null, null, null) );
		root.setNodeFor(Answer.YES, nd);
		
		DecisionNode ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags(null, AuthenticationType.Password, null, null) );
		nd.setNodeFor(Answer.NO, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags(DuaAgreementMethod.Sign, null, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		nd = ndSub;
		
		ndSub = new DecisionNode( Integer.toString(++id) );
		ndSub.setBaseAssumption( new DataTags( null, null, null, null) );
		nd.setNodeFor(Answer.YES, ndSub);
		
		DataTags expected = new DataTags( DuaAgreementMethod.Sign,
				AuthenticationType.Password, null, null);
		DataTags result = ndSub.getAbsoluteAssumption();
		
		assertEquals( expected, result );
	}
	
}
