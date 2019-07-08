/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions;

import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;

/**
 *
 * @author mor
 */
public class EqualsExp extends BooleanExpression {
    
    private AtomicValue valueInSlot;
    private AtomicValue value;

    public EqualsExp(AtomicValue valueInSlot, AtomicValue value) {
        this.valueInSlot = valueInSlot;
        this.value = value;
    }

    @Override
    public boolean evaluate(CompoundValue ps) {
        return (valueInSlot.compare(value) == AtomicValue.CompareResult.Same);
    }
    
}
