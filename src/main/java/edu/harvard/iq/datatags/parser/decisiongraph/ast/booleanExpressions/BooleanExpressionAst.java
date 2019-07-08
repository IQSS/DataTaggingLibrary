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

        void visit(GreaterThanExpAst exp);

        void visit(SmallerThanExpAst exp);

        void visit(EqualsExpAst exp);

        void visit(NotExpAst exp);

        void visit(AndExpAst exp);
        
        void visit(TrueExpAst exp);

    }
    
    public abstract void accept(Visitor v);
}
