package edu.harvard.iq.datatags.parser.tagspace.ast;

import java.util.List;
import org.codehaus.jparsec.internal.util.Objects;

/**
 * Definition of a slot that has named slots under it:
 * {@code CompoundSlot: consists of Alpha, Beta, Gamma.}
 * @author michael
 */
public class CompoundSlot extends AbstractSlot {
    
    private final List<String> subSlotNames;

    public CompoundSlot(String aName, String aNote, List<String> someSubSlotNames) {
        super(aName, aNote);
        subSlotNames = someSubSlotNames;
    }

    public List<String> getSubSlotNames() {
        return subSlotNames;
    }
    
    @Override
    public <R> R accept(AbstractSlot.Visitor<R> visitor ) {
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
        
        if ( obj instanceof CompoundSlot ) {
            CompoundSlot other = (CompoundSlot) obj;
            return Objects.equals(getSubSlotNames(), other.getSubSlotNames())
                    && super.equals(obj);
            
        } else {
            return false;
        }
        
    }
    
    
    
}
