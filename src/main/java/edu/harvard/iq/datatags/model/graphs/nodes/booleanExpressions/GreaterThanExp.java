/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions;

import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.List;

/**
 *
 * @author mor
 */
public class GreaterThanExp extends BooleanExpression {
    
    private List<String> fullyQualifiedSlotName;;
    private AtomicValue value;

    public GreaterThanExp(List<String> fullyQualifiedSlotName, AtomicValue value) {
        this.fullyQualifiedSlotName = fullyQualifiedSlotName;
        this.value = value;
    }
    
    @Override
    public boolean evaluate(CompoundValue ps) {
        return (ps.getValue(fullyQualifiedSlotName).compare(value).equals(AbstractValue.CompareResult.Bigger));
    }
    
}
