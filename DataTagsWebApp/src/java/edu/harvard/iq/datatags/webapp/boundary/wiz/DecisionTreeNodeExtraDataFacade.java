/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.webapp.boundary.wiz;

import edu.harvard.iq.datatags.webapp.model.DecisionTreeNodeExtraData;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author michael
 */
@Stateless
public class DecisionTreeNodeExtraDataFacade extends AbstractFacade<DecisionTreeNodeExtraData> {
	@PersistenceContext(unitName = "PrivacyTagsWebAppPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public DecisionTreeNodeExtraDataFacade() {
		super(DecisionTreeNodeExtraData.class);
	}
	
}
