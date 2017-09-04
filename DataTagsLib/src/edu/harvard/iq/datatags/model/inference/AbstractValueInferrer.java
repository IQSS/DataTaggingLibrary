package edu.harvard.iq.datatags.model.inference;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Base class for value inferrers. Manages subspaces and the inferred values.
 * 
 * The minimal coordinates in each pair create a series of hierarchical 
 * sub-spaces, where the spaces of later pairs are contained in the spaces of
 * the earlier ones. Calling {@link #apply(edu.harvard.iq.datatags.model.values.CompoundValue)}
 * composes the the inValue with the {@code inferredValue} of least coordinate 
 * that supports it.
 * 
 * @author mor_vilozni
 * @author michael
 */
public abstract class AbstractValueInferrer {
    
    public static class InferencePair {

        CompoundValue minimalCoordinate;
        CompoundValue inferredValue;

        public InferencePair(CompoundValue minimalCoordinate, CompoundValue inferredValue) {
            this.minimalCoordinate = minimalCoordinate;
            this.inferredValue = inferredValue;
        }

        public CompoundValue getMinimalCoordinate() {
            return minimalCoordinate;
        }
        
        public CompoundValue getInferredValue() {
            return inferredValue;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.minimalCoordinate);
            hash = 97 * hash + Objects.hashCode(this.inferredValue);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final InferencePair other = (InferencePair) obj;
            if (!Objects.equals(this.minimalCoordinate, other.minimalCoordinate)) {
                return false;
            }
            return Objects.equals(this.inferredValue, other.inferredValue);
        }
    }
    
    protected final List<InferencePair> inferencePairs = new ArrayList<>();

    public abstract CompoundValue apply( CompoundValue inValue );
    
    public boolean add(InferencePair e) {
        return inferencePairs.add(e);
    }

    public List<InferencePair> getInferencePairs() {
        return inferencePairs;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.inferencePairs);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final SupportValueInferrer other = (SupportValueInferrer) obj;
        return Objects.equals(this.inferencePairs, other.inferencePairs);
    }
    
}
