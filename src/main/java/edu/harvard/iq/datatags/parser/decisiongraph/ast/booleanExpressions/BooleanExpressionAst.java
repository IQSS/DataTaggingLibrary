/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions;

import edu.harvard.iq.datatags.model.graphs.nodes.booleanExpressions.BooleanExpression;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;

/**
 *
 * @author mor
 */
public abstract class BooleanExpressionAst {
    
    abstract BooleanExpression build();
    
    public abstract String getSlotType();
    
    public interface Visitor<T> {

        T visit(GreaterThanExpAst exp);

        T visit(SmallerThanExpAst exp);

        T visit(EqualsExpAst exp);

        T visit(NotExpAst exp);

        T visit(AndExpAst exp);
        
        T visit(TrueExpAst exp);

    }
    
    public abstract <T> T accept(Visitor<T> v);
}
