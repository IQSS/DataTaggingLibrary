package edu.harvard.iq.datatags.model.graphs;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A single answer, given to a {@link AskNode} by the user. In order to 
 * allow object reuse (there are typically a lot of similar answers in a questionnaire)
 * instances may be pooled, which the class constructor is private. 
 * 
 * Use {@link #get(java.lang.String)} or statically import
 * {@link #Answer(java.lang.String)} to obtain/generate instances.
 * 
 * @author michael
 */
public class Answer {
	private final String answerText;
	private static final Map<String, Answer> ANSWER_POOL = new ConcurrentHashMap<>();
	public static final Answer YES;
	public static final Answer NO;
	
    public static Answer get(String anAnswerText) {
        return Answer(anAnswerText);
    }
    
	public static Answer Answer( String anAnswerText ) {
        String canonical = anAnswerText.intern();
		return ANSWER_POOL.computeIfAbsent(canonical,  s -> new Answer(s) );
	}
	
    static {
        YES = Answer("yes");
        NO  = Answer("no");
    }
    
	private Answer(String anAnswerText) {
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
