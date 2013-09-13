package edu.harvard.iq.datatags.webapp.view;

import edu.harvard.iq.datatags.webapp.boundary.ExplanationFacade;
import edu.harvard.iq.datatags.webapp.model.Explanation;
import java.util.List;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 * View bean for the explanation list page
 * @author michael
 */
@ManagedBean
@RequestScoped
public class ExplanationListView {
	
	@EJB
	private ExplanationFacade explanationFcd;
	
	@ManagedProperty(value="#{param.name}")
	private String explanationName;
	
	private Explanation explanation;
	
	private boolean explanationNotFound;
	private boolean explanationIsNew;
	
	/**
	 * Creates a new instance of ExplanationListView
	 */
	public ExplanationListView() {
	}
	
	public long getExplanationCount() {
		return explanationFcd.count();
	}
	
	public List<Explanation> getExplanations() {
		return explanationFcd.findAll();
	}
	
	public Explanation getExplanation() {
		return explanation;
	}

	public String getExplanationName() {
		return explanationName;
	}

	public void setExplanationName(String explanationName) {
		this.explanationName = explanationName;
		if ( isExplanationNameValid() ) {
			explanationName = explanationName.trim();
			explanation = explanationFcd.findByName(explanationName);
			if ( explanation == null ) {
				explanationNotFound = true;
			}
		} else {
			explanationIsNew = true;
		}
		if ( explanation == null ) {
			explanation =  new Explanation();
		}
	}
	
	public boolean isExplanationNameValid() {
		return explanationName != null && !(explanationName.trim().isEmpty());
	}
	
	public boolean isExplanationNotFound() {
		return explanationNotFound;
	}

	public boolean isExplanationNew() {
		return explanationIsNew;
	}
	
	public String save(String dest) {
		explanationFcd.create(explanation);
		explanation = new Explanation();
		return dest;
	}
	
	public String update() {
		explanationFcd.edit(explanation);
		return "UPDATE_OK";
	}

}
