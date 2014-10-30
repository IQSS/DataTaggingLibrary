package edu.harvard.iq.datatags.model.values;

import java.util.Objects;

/**
 * A single answer, given to a {@link DecisionTreeNode} by the user.
 * @author michael
 */
public class Answer {
	private final String answerText;
	
	public static final Answer YES = Answer("yes");
	public static final Answer NO  = Answer("no");
	
	public static Answer Answer( String anAnswerText ) {
        // TODO consider internalizing instances! and then make "new" private. 
		return new Answer( anAnswerText );
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
		return "[Answer answerText:" + answerText + ']';
	}
	
	
}
