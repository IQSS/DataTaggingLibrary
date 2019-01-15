package edu.harvard.iq.datatags.model.inference;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Optional;

/**
 * A value inferrer that applies the tag of the maximal coordinate whose 
 * compliance space contains the passed value.
 * 
 * Example usage: decide on a prescriptive slot (e.g. encryption) based on 
 * descriptive ones (e.g. harm, effortForHarm).
 * 
 * @author michael
 */
public class ComplianceValueInferrer extends AbstractValueInferrer {
    
    
    @Override
    public CompoundValue apply( CompoundValue inValue ) {
        
        CompoundValue out = inValue;
        CompoundValue last;
        do {
            last = out;
            // find the first minimal coordiante that's bigger than inValue
            Optional<InferencePair> innermostSupport = getInferencePairs().stream()
                    .filter(pair -> {
                        CompoundValue projectedIn = inValue.project(pair.minimalCoordinate.getNonEmptySubSlots());
                        return projectedIn.compare(pair.minimalCoordinate).isGtE;
                    }).reduce( (_a,b)->b ); // gets the last.

            // compose
            if ( innermostSupport.isPresent() ) {
                out = last.composeWith(innermostSupport.get().inferredValue);
            }
        } while ( ! out.equals(last) ); // repeat if necessary
        
        return out;
    }
    
    @Override
    public boolean equals( Object other ) {
        if ( other == this ) return true;
        if ( other == null ) return false;
        return ( other instanceof ComplianceValueInferrer ) 
            ? super.equals(other)
            : false;
    }

    @Override
    public int hashCode() {
        return super.hashCode()*17;
    }
    
}
