package edu.harvard.iq.datatags.parser.tagspace.ast;

import java.util.List;

/**
 * Definition of a slot that can hold a set of values.
 * {@code SampleAggregateSlot: some of alpha, beta, gamma.}
 * @author michael
 */
public class AggregateSlot extends AbstractSimpleSlot {
    
    public AggregateSlot(String aName, String aNote, List<ValueDefinition> valueDefinitions) {
        super(aName, aNote, valueDefinitions);
    }
    
    @Override
    public <R> R accept(AbstractSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
    
}
