package edu.harvard.iq.policymodels.model.inference;

import edu.harvard.iq.policymodels.model.policyspace.values.CompoundValue;
import java.util.Optional;

/**
 * Value inferrer that applies the values pointed by the minimal coordinate 
 * supporting the passed value. I.e. snap to the top of the support space.
 * 
 * Example usage is for setting which policy/DataTag is needed, based on 
 * some prescriptive dimensions.
 * 
 * 
 * @author mor_vilozni
 * @author michael
 */
public class SupportValueInferrer extends AbstractValueInferrer {

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
    @Override
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
    

    @Override
    public boolean equals( Object other ) {
        if ( other == this ) return true;
        if ( other == null ) return false;
        return ( other instanceof SupportValueInferrer ) 
            ? super.equals(other)
            : false;
    }

    @Override
    public int hashCode() {
        return super.hashCode()*31;
    }
    
    
}
