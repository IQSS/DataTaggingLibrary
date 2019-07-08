/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions;

import java.util.List;

/**
 *
 * @author mor
 */
public abstract class AtomicExpressionAst extends BooleanExpressionAst {
    
    public abstract List<String> getValueInSlot();
    
    public abstract String getValue();
    
}
