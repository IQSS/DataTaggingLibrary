package edu.harvard.iq.datatags.questionnaire;

import edu.harvard.iq.datatags.tags.DataTags;
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
public class DecisionNode {
	
	private final String id;
	private DecisionNode parent;
	private String title;
	private String questionText;
	private String helpText;
	private final Map<Answer, DecisionNode> subtrees = new EnumMap<>(Answer.class);
	private DataTags baseAssumption = new DataTags();

	public DecisionNode(String id) {
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

	public DataTags getBaseAssumption() {
		return baseAssumption;
	}

	public void setBaseAssumption(DataTags baseAssumption) {
		this.baseAssumption = baseAssumption;
	}

	public DecisionNode getParent() {
		return parent;
	}

	protected void setParent(DecisionNode parent) {
		this.parent = parent;
	}
	
	public void setNodeFor( Answer answer, DecisionNode node ) {
		if ( node != null ) {
			node.setParent(this);
		}
		subtrees.put(answer, node);
	}
	
	public DecisionNode getNodeFor( Answer answer ) {
		return subtrees.get(answer);
	}
	
	public DataTags getAbsoluteAssumption() {
		List<DecisionNode> ancestors = new LinkedList<>();
		DecisionNode node = this;
		while ( node != null ) {
			ancestors.add(node);
			node = node.getParent();
		}
		Collections.reverse(ancestors);
		DataTags result = new DataTags();
		for ( DecisionNode dtn : ancestors ) {
			result = dtn.getBaseAssumption().composeWith(result);
		}
		
		return result;
	}

	@Override
	public String toString() {
		return "DecisionNode{" + "id=" + id + ", title=" + title + '}';
	}
	
	
}
