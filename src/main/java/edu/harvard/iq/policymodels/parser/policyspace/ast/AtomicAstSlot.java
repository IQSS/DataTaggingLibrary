package edu.harvard.iq.policymodels.parser.policyspace.ast;

import java.util.List;

/**
 * Definition of a slot that can hold a single value.
 * {@code AtomicSlot: one of alpha, beta, gamma.}
 * 
 * @author michael
 */
public class AtomicAstSlot extends AbstractSimpleAstSlot {
    
    public AtomicAstSlot(String name, String note, List<ValueDefinition> someValueNames ) {
        super(name, note, someValueNames);
    }
    
    @Override
    public <R> R accept(AbstractAstSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
}
