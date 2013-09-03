/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.privacytags.webapp.boundary;

import edu.harvard.iq.privacytags.model.questionnaire.Answer;
import edu.harvard.iq.privacytags.model.questionnaire.DecisionTreeNode;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ApplicationScoped;

/**
 *
 * @author michael
 */
@ManagedBean
@ApplicationScoped
public class App {
	
	private DecisionTreeNode questionnaireRoot;
	private final Map<String, DecisionTreeNode> dtnById = new HashMap<>(); 
	/**
	 * Creates a new instance of App
	 */
	public App() {
	}
	
	public void setQuestionnaire( DecisionTreeNode root ) {
		dtnById.clear();
		questionnaireRoot = root;
		List<DecisionTreeNode> queue = new LinkedList<>();
		queue.add( questionnaireRoot );
		while ( ! queue.isEmpty() ) {
			DecisionTreeNode nd = queue.remove(0);
			dtnById.put( nd.getId(), nd );
			for ( Answer a : Answer.values() ) {
				DecisionTreeNode subNode = nd.getNodeFor(a);
				if ( subNode != null ) {
					queue.add( subNode );
				}
			}
		}
	}
	
}
