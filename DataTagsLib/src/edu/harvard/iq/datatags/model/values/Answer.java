package edu.harvard.iq.datatags.model.values;

import java.util.Objects;

/**
 * A single answer, given to a {@link DecisionTreeNode} by the user.
 * @author michael
 */
public class Answer {
	private final String answerName;
	
	public static final Answer YES = Answer("Yes");
	public static final Answer NO  = Answer("No");
	
	public static Answer Answer( String aName ) {
		return new Answer( aName );
	}
	
	public Answer(String answerName) {
		this.answerName = answerName;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.answerName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if ( ! (obj instanceof Answer) ) {
			return false;
		}
		final Answer other = (Answer) obj;
		return Objects.equals(this.answerName, other.answerName);
	}

	@Override
	public String toString() {
		return "[Answer answerName:" + answerName + ']';
	}
	
	
}
