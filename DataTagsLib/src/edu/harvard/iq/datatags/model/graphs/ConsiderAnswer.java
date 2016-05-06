package edu.harvard.iq.datatags.model.graphs;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A single consider-answer, given to a {@link edu.harvard.iq.datatags.model.graphs.nodes.ConsiderNode} . In order to allow
 * object reuse (there are typically a lot of similar answers in a
 * questionnaire) instances may be pooled, which the class constructor is
 * private.
 *
 * Use {@link #get(edu.harvard.iq.datatags.model.values.CompoundValue)} or statically import
 * {@link #Answer(edu.harvard.iq.datatags.model.values.CompoundValue)} to obtain/generate instances.
 *
 */
public class ConsiderAnswer {

    private final CompoundValue answer;
    private static final Map<CompoundValue, ConsiderAnswer> ANSWER_POOL = new ConcurrentHashMap<>();

    public static ConsiderAnswer get(CompoundValue ans) {
        return Answer(ans);
    }

    public static ConsiderAnswer Answer(CompoundValue ans) {

        return ANSWER_POOL.computeIfAbsent(ans, s -> new ConsiderAnswer(s));
    }

    public CompoundValue getAnswer() {
        return answer;
    }

    private ConsiderAnswer(CompoundValue ans) {
        answer = ans;
    }

    public String getAnswerText() {
        return answer.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.answer);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConsiderAnswer)) {
            return false;
        }
        final ConsiderAnswer other = (ConsiderAnswer) obj;
        return Objects.equals(this.answer, other.answer);
    }

    @Override
    public String toString() {
        return "[Answer:" + answer + ']';
    }

}
