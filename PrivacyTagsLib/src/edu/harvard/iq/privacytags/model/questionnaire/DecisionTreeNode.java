package edu.harvard.iq.privacytags.model.questionnaire;

import edu.harvard.iq.privacytags.model.PrivacyTagSet;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A node in the decision tree. From the user standpoint, this is a 
 * single question, with a YES/NO {@link Answer}.
 * @author michael
 */
public class DecisionTreeNode {
	
	private final String id;
	private DecisionTreeNode parent;
	private String title;
	private String questionText;
	private String helpText;
	private final Map<Answer, DecisionTreeNode> subtrees = new EnumMap<>(Answer.class);
	private PrivacyTagSet baseAssumption = new PrivacyTagSet();

	public DecisionTreeNode(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getQuestionText() {
		return questionText;
	}

	public void setQuestionText(String questionText) {
		this.questionText = questionText;
	}

	public String getHelpText() {
		return helpText;
	}

	public void setHelpText(String helpText) {
		this.helpText = helpText;
	}

	public PrivacyTagSet getBaseAssumption() {
		return baseAssumption;
	}

	public void setBaseAssumption(PrivacyTagSet baseAssumption) {
		this.baseAssumption = baseAssumption;
	}

	public DecisionTreeNode getParent() {
		return parent;
	}

	protected void setParent(DecisionTreeNode parent) {
		this.parent = parent;
	}
	
	public void setNodeFor( Answer answer, DecisionTreeNode node ) {
		if ( node != null ) {
			node.setParent(this);
		}
		subtrees.put(answer, node);
	}
	
	public DecisionTreeNode getNodeFor( Answer answer ) {
		return subtrees.get(answer);
	}
	
	public PrivacyTagSet getAbsoluteAssumption() {
		List<DecisionTreeNode> ancestors = new LinkedList<>();
		DecisionTreeNode node = this;
		while ( node != null ) {
			ancestors.add(node);
			node = node.getParent();
		}
		Collections.reverse(ancestors);
		PrivacyTagSet result = new PrivacyTagSet();
		for ( DecisionTreeNode dtn : ancestors ) {
			result = dtn.getBaseAssumption().composeWith(result);
		}
		
		return result;
	}
}
