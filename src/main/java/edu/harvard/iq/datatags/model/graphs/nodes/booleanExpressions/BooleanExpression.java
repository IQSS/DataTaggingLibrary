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
public abstract class BooleanExpression {
    
    abstract public boolean evaluate(CompoundValue ps);
    
}
