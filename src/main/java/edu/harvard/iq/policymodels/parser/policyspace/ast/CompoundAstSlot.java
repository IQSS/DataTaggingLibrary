package edu.harvard.iq.policymodels.parser.policyspace.ast;

import java.util.List;
import org.jparsec.internal.util.Objects;

/**
 * Definition of a slot that has named slots under it:
 * {@code CompoundSlot: consists of Alpha, Beta, Gamma.}
 * @author michael
 */
public class CompoundAstSlot extends AbstractAstSlot {
    
    private final List<String> subSlotNames;

    public CompoundAstSlot(String aName, String aNote, List<String> someSubSlotNames) {
        super(aName, aNote);
        subSlotNames = someSubSlotNames;
    }

    public List<String> getSubSlotNames() {
        return subSlotNames;
    }
    
    @Override
    public <R> R accept(AbstractAstSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
    
    @Override
    protected String toStringExtras() {
        return "subSlotNames: " + getSubSlotNames();
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
        if ( obj == null ) return false;
        if ( obj == this ) return true;
        
        if ( obj instanceof CompoundAstSlot ) {
            CompoundAstSlot other = (CompoundAstSlot) obj;
            return Objects.equals(getSubSlotNames(), other.getSubSlotNames())
                    && super.equals(obj);
            
        } else {
            return false;
        }
        
    }
    
    
    
}
