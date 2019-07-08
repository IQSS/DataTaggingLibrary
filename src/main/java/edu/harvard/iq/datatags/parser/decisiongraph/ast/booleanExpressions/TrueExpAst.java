/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions;

import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.BooleanExpression;
import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.TrueExp;

/**
 *
 * @author mor
 */
public class TrueExpAst extends BooleanExpressionAst{

    @Override
    BooleanExpression build() {
        return new TrueExp();
    }

    @Override
    public String getSlotType() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void accept(Visitor v) {
        v.visit(this);
    }
    
}
