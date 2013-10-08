package edu.harvard.iq.datatags.webapp.view;

import edu.harvard.iq.datatags.tags.DataTags;
import edu.harvard.iq.datatags.runtime.Answer;
import edu.harvard.iq.datatags.questionnaire.DecisionNode;
import edu.harvard.iq.datatags.tags.AuthenticationType;
import edu.harvard.iq.datatags.tags.DuaAgreementMethod;
import edu.harvard.iq.datatags.tags.EncryptionType;
import edu.harvard.iq.datatags.tags.HarmLevel;
import edu.harvard.iq.datatags.webapp.boundary.App;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.RequestScoped;

/**
 *
 * @author michael
 */
@ManagedBean
@RequestScoped
public class DecisionNodeView {
	
	private static final Logger logger = Logger.getLogger(DecisionNodeView.class.getName());
	
	@ManagedProperty(value="#{param.nodeId}")
	private String nodeId;
	
	@ManagedProperty( value="#{App}" )
	private App app;
	
	/**
	 * Creates a new instance of DecisionNodeView
	 */
	public DecisionNodeView() {
	}

	public String getNodeId() {
		return nodeId;
	}

	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}

	public App getApp() {
		return app;
	}

	public void setApp(App app) {
		this.app = app;
	}
	
	public DecisionNode getNode() {
		return app.getDecisionNode(nodeId);
	}
	
	public DecisionNode getYesNode() {
		return hasNode() ? getNode().getNodeFor(Answer.YES) : null;
	}
	
	public boolean hasYesNode() {
		return getYesNode() != null;
	}
	
	public DecisionNode getNoNode() {
		return hasNode() ? getNode().getNodeFor(Answer.NO) : null;
	}
	
	public boolean hasNoNode() {
		return getNoNode() != null;
	}
	
	public DecisionNode getParentNode() {
		return hasNode() ? getNode().getParent() : null;
	}
	
	public boolean hasParentNode() {
		return getParentNode() != null;
	}
	
	public boolean hasNode() {
		return getNode() != null;
	}
	
	public boolean hasNodeFor( Answer a ) {
		return hasNode() && (getNode().getNodeFor(a)!=null);
	}
	
	public DataTags tags() {
		return hasNode() ? getNode().getAbsoluteAssumption() : null;
	}
	
	public boolean areTagsComplete() {
		return (tags() != null) && tags().isComplete();
	}
	
	public DataTags diffPrivacyTags() {
		DataTags dt = tags();
		if ( dt == null ) return null;
		
		DataTags res = dt.makeCopy();
		
		if (res.getTransitEncryptionType() != EncryptionType.Clear ) {
			res.setTransitEncryptionType(EncryptionType.Encrypted);
		}
		// Storage is not part of this access-only layer
		res.setStorageEncryptionType(null);
		
		if ( res.getDuaAgreementMethod() != null ) {
			if ( res.getDuaAgreementMethod().ordinal() > 0 ) { 
				res.setDuaAgreementMethod(DuaAgreementMethod.values()[res.getDuaAgreementMethod().ordinal()-1]);
			}
			return res;
		}
		
		if ( res.getAuthenticationType() != null ) {
			if ( res.getAuthenticationType().ordinal() > 0 ) { 
				res.setAuthenticationType(AuthenticationType.values()[res.getAuthenticationType().ordinal()-1]);
			}
			return res;
		}
		
		return res;
	}
	
	public HarmLevel harmLevelForTags() {
		return HarmLevel.levelFor( tags() );
	}
	
	public HarmLevel harmLevelForDiffTags() {
		DataTags df = diffPrivacyTags();
		df.setTransitEncryptionType(EncryptionType.Clear);
		df.setStorageEncryptionType(EncryptionType.Clear);
				
		return HarmLevel.levelFor( df );
	}
}
