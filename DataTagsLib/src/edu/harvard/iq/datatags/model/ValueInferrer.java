package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Infers a value of based on a set of other values. The inference values are
 * ordered in increasing order.
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
public class ValueInferrer {
    
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
    
    private final List<InferencePair> inferencePairs =  new ArrayList<>();

    /**
     * Composes the least value in {@code this} that supports {@code inValue}.
     * 
     * "support" used here in the sense of "support space".
     * 
     * Supports self-loops, in case an intermediate inferred value should invoke another
     * inference trigger.
     * 
     * @param inValue 
     * @return A new value, inferred from {@code inValue}, using the inference pairs.
     */
    public CompoundValue apply( CompoundValue inValue ) {
        
        CompoundValue out = inValue;
        CompoundValue last;
        do {
            last = out;
            // find the first minimal coordiante that's bigger than inValue
            Optional<InferencePair> firstSupporting = getInferencePairs().stream()
                    .filter(pair -> {
                        CompoundValue projectedIn = inValue.project(pair.minimalCoordinate.getNonEmptySubSlots());
                        return pair.minimalCoordinate.compare(projectedIn).isGtE;
                    }).findFirst();

            // compose
            if ( firstSupporting.isPresent() ) {
                out = last.composeWith(firstSupporting.get().inferredValue);
            }
        } while ( ! out.equals(last) ); // repeat if necessary
        
        return out;
    }
    
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
        final ValueInferrer other = (ValueInferrer) obj;
        return Objects.equals(this.inferencePairs, other.inferencePairs);
    }

    
    
    
}
