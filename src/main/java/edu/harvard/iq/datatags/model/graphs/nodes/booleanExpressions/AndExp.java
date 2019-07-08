/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions;

import edu.harvard.iq.datatags.model.values.CompoundValue;

/**
 *
 * @author mor
 */
public class AndExp extends BooleanExpression{

    private BooleanExpression op1;
    private BooleanExpression op2;

    public AndExp(BooleanExpression op1, BooleanExpression op2) {
        this.op1 = op1;
        this.op2 = op2;
    }
    
    @Override
    public boolean evaluate(CompoundValue ps) {
        return op1.evaluate(ps) && op2.evaluate(ps);
    }
    
}
