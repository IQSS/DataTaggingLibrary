/*
 *  (C) Michael Bar-Sinai
 */
package edu.harvard.iq.datatags.parser.definitions.ast;

import java.util.List;

/**
 * Definition of a slot that can hold a single value.
 * {@code AtomicSlot: one of alpha, beta, gamma.}
 * 
 * @author michael
 */
public class AtomicSlot extends AbstractSimpleSlot {
    
    public AtomicSlot(String name, String note, List<ValueDefinition> someValueNames ) {
        super(name, note, someValueNames);
    }
    
    @Override
    public <R> R accept(AbstractSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
}
