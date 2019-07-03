/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.Objects;

/**
 *
 * @author mor
 */
public class AndExpAst extends BooleanExpressionAst {
    
    private BooleanExpressionAst op1;
    private BooleanExpressionAst op2;

    public AndExpAst(BooleanExpressionAst op1, BooleanExpressionAst op2) {
        this.op1 = op1;
        this.op2 = op2;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + Objects.hashCode(this.op1);
        hash = 59 * hash + Objects.hashCode(this.op2);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AndExpAst other = (AndExpAst) obj;
        if (!Objects.equals(this.op1, other.op1)) {
            return false;
        }
        if (!Objects.equals(this.op2, other.op2)) {
            return false;
        }
        return true;
    }

    public BooleanExpressionAst getFirstOp() {
        return op1;
    }

    public BooleanExpressionAst getSecondOp() {
        return op2;
    }
    
}
