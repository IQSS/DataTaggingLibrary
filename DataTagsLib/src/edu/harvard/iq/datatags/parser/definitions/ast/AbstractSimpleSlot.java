package edu.harvard.iq.datatags.parser.definitions.ast;

import java.util.List;
import java.util.Objects;

/**
 * A slot that does not contain other slots, i.e {@link AtomicSlot} or (@link CompoundSlot}.
 * @author michael
 */
public abstract class AbstractSimpleSlot extends AbstractSlot {
    
    private final List<ValueDefinition> valueDefinitions;

    public AbstractSimpleSlot(String aName, String aNote, List<ValueDefinition> valueDefinitions) {
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
            AbstractSimpleSlot other = (AbstractSimpleSlot) obj;
            return Objects.equals(getValueDefinitions(), other.getValueDefinitions())
                    && super.equals(obj);
            
        } else {
            return false;
        }
        
    }
    
}
