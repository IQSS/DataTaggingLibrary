package edu.harvard.iq.datatags.model.values;

import java.util.Objects;

/**
 * A single answer, given to a {@link DecisionTreeNode} by the user.
 * @author michael
 */
public class Answer {
	private final String answerText;
	
	public static final Answer YES = Answer("Yes");
	public static final Answer NO  = Answer("No");
	
	public static Answer Answer( String aName ) {
		return new Answer( aName );
	}
	
	public Answer(String anAnswerText) {
		answerText = anAnswerText;
	}

	public String getAnswerText() {
		return answerText;
	}
	
	@Override
	public int hashCode() {
		int hash = 7;
		hash = 13 * hash + Objects.hashCode(this.answerText);
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
		return Objects.equals(this.answerText, other.answerText);
	}

	@Override
	public String toString() {
		return "[Answer answerName:" + answerText + ']';
	}
	
	
}
