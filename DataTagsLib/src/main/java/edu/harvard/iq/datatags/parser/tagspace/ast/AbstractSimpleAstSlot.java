package edu.harvard.iq.datatags.parser.tagspace.ast;

import java.util.List;
import java.util.Objects;

/**
 * A slot that does not contain other slots, i.e {@link AtomicAstSlot} or (@link CompoundAstSlot}.
 * @author michael
 */
public abstract class AbstractSimpleAstSlot extends AbstractAstSlot {
    
    private final List<ValueDefinition> valueDefinitions;

    public AbstractSimpleAstSlot(String aName, String aNote, List<ValueDefinition> valueDefinitions) {
        super(aName, aNote);
        this.valueDefinitions = valueDefinitions;
    }

    public List<ValueDefinition> getValueDefinitions() {
        return valueDefinitions;
    }
    
    @Override
    protected String toStringExtras() {
        return "valueDefinitions:" + valueDefinitions.toString();
    }
    
    @Override
    public int hashCode() {
        return super.hashCode();
    }
    
    @Override
    public boolean equals( Object obj ) {
        if ( obj==null ) return false;
        if ( obj==this ) return true;
        
        if ( obj.getClass() == getClass() ) {
            AbstractSimpleAstSlot other = (AbstractSimpleAstSlot) obj;
            return Objects.equals(getValueDefinitions(), other.getValueDefinitions())
                    && super.equals(obj);
            
        } else {
            return false;
        }
        
    }
    
}
