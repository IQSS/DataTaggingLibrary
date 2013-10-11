package edu.harvard.iq.datatags.runtime;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.model.DataTags;
import java.util.EnumMap;
import java.util.Map;

/**
 * A node where the user has to decide on a yes/no question.
 * @author michael
 */
public class DecisionNode extends Node {
	
	private DataTags tags;
	private final Map<Answer, Node> nextNodeByAnswer = new EnumMap<>(Answer.class);
	
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
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visitDecisionNode(this);
	}
	
	/**
	 * Sets the node for the passed answer
	 * @param <T> the actual type of the node
	 * @param answer the answer for which the node applies
	 * @param node the node
	 * @return {@code node}, for convenience, call chaining, etc.
	 */
	public <T extends Node> T setNodeFor( Answer answer, T node ) {
		nextNodeByAnswer.put(answer, node);
		return node;
	}
	
	public Node getNodeFor( Answer answer ) {
		return nextNodeByAnswer.get(answer);
	}
	
}
