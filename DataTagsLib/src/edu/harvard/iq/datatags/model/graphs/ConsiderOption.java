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
 * {@link #make(edu.harvard.iq.datatags.model.values.CompoundValue)} to obtain/generate instances.
 *
 * TODO: This class is intended for removal (See issue #185).
 * 
 */
public class ConsiderOption {

    private final CompoundValue value;
    private static final Map<CompoundValue, ConsiderOption> OPTION_POOL = new ConcurrentHashMap<>();

    public static ConsiderOption get(CompoundValue ans) {
        return make(ans);
    }

    public static ConsiderOption make(CompoundValue ans) {
        return OPTION_POOL.computeIfAbsent(ans, s -> new ConsiderOption(s));
    }

    public CompoundValue getValue() {
        return value;
    }

    private ConsiderOption(CompoundValue ans) {
        value = ans;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 13 * hash + Objects.hashCode(this.value);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ConsiderOption)) {
            return false;
        }
        final ConsiderOption other = (ConsiderOption) obj;
        return Objects.equals(this.value, other.value);
    }

    @Override
    public String toString() {
        return "[Answer:" + value + ']';
    }

}
