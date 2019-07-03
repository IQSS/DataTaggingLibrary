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
public class NotExpAst extends BooleanExpressionAst {
    
    private BooleanExpressionAst op;

    public NotExpAst(BooleanExpressionAst op) {
        this.op = op;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + Objects.hashCode(this.op);
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
        final NotExpAst other = (NotExpAst) obj;
        if (!Objects.equals(this.op, other.op)) {
            return false;
        }
        return true;
    }

    public BooleanExpressionAst getOp() {
        return op;
    }
    
    
}
