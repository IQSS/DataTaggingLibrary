package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.tags.DataTags;
import java.util.EnumMap;
import java.util.Map;

/**
 * A node where the user has to decide on a yes/no question.
 * @author michael
 */
public class DecisionNode extends Node {
	
	private DataTags tags;
	private final Map<Answer, DecisionNode> nextNodeByAnswer = new EnumMap<>(Answer.class);
	
	public DecisionNode(String id, String title) {
		this(id, title, null, null);
	}

	public DecisionNode(String id) {
		this(id, null);
	}

	public DecisionNode(String id, String title, String text, FlowChart chart) {
		super(id, title, text, chart);
	}

	public DataTags getTags() {
		return tags;
	}

	public void setTags(DataTags tags) {
		this.tags = tags;
	}
	
	
	public void setNodeFor( Answer answer, DecisionNode node ) {
		nextNodeByAnswer.put(answer, node);
	}
	
	public DecisionNode getNodeFor( Answer answer ) {
		return nextNodeByAnswer.get(answer);
	}
	
}
