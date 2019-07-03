/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions;

import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;

/**
 *
 * @author mor
 */
public class GreaterThanExp extends BooleanExpression {
    
    private AtomicValue valueInSlot;
    private AtomicValue value;

    public GreaterThanExp(AtomicValue valueInSlot, AtomicValue value) {
        this.valueInSlot = valueInSlot;
        this.value = value;
    }
    
    @Override
    public boolean gen(CompoundValue ps) {
        return (valueInSlot.compare(value) == AtomicValue.CompareResult.Bigger);
    }
    
}
