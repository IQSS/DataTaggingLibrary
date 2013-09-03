package edu.harvard.iq.privacytags.webapp.boundary;

import edu.harvard.iq.privacytags.webapp.model.DecisionTreeNodeExtraData;
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
