/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.webapp.boundary;

import edu.harvard.iq.datatags.webapp.model.Explanation;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author michael
 */
@Stateless
public class ExplanationFacade extends AbstractFacade<Explanation> {
	@PersistenceContext(unitName = "PrivacyTagsWebAppPU")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ExplanationFacade() {
		super(Explanation.class);
	}
	
	public Explanation findByName( String name ) {
		return null;
	}
}
