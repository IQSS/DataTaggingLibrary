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
public class TrueExp extends BooleanExpression {

    @Override
    public boolean evaluate(CompoundValue ps) {
        return true;
    }
     
    private TrueExp() {
        
    }
    
    public static final TrueExp INSTANCE = new TrueExp();
    
}
