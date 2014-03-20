package edu.harvard.iq.datatags.model.charts.nodes;

import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * A node where the user has to decide on a yes/no question.
 * @author michael
 */
public class AskNode extends Node {
	private final Map<Answer, Node> nextNodeByAnswer = new HashMap<>();
	
	public AskNode(String id, String title) {
		this(id, title, null, null);
	}

	public AskNode(String id) {
		this(id, null);
	}

	public AskNode(String id, String title, String text, FlowChart chart) {
		super(id, title, text, chart);
	}
	
	@Override
	public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
		return vr.visitAskNode(this);
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
	
	public Set<Answer> getAnswers() {
		return nextNodeByAnswer.keySet();
	}
	
	public Node getNodeFor( Answer answer ) {
		return nextNodeByAnswer.get(answer);
	}
	
}
