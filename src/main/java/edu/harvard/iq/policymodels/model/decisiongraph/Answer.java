package edu.harvard.iq.policymodels.model.decisiongraph;

import edu.harvard.iq.policymodels.model.decisiongraph.nodes.AskNode;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A single answer, given to a {@link AskNode} by the user. In order to 
 * allow object reuse (there are typically a lot of similar answers in a questionnaire)
 * instances may be pooled, which is why the class constructor is private. 
 * 
 * Use {@link #withName(java.lang.String)} to obtain/generate instances.
 * 
 * @author michael
 */
public class Answer {
	private static final Map<String, Answer> ANSWER_POOL = new ConcurrentHashMap<>();
	public static final Answer YES;
	public static final Answer NO;
	
	private final String answerText;
    
    public static Answer withName(String anAnswerText) {
        String canonical = anAnswerText.intern();
		return ANSWER_POOL.computeIfAbsent(canonical,  s -> new Answer(s) );
	}
	
    static {
        // Initialize now, when ANSWER_POOL is there.
        YES = withName("yes");
        NO  = withName("no");
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
