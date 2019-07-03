/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast.booleanExpressions;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mor
 */
public class GreaterThanExpAst extends BooleanExpressionAst {

    private List<String> valueInSlot;
    private String value;

    public GreaterThanExpAst(List<String> valueInSlot, String value) {
        this.valueInSlot = valueInSlot;
        this.value = value;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.valueInSlot);
        hash = 67 * hash + Objects.hashCode(this.value);
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
        final GreaterThanExpAst other = (GreaterThanExpAst) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.valueInSlot, other.valueInSlot)) {
            return false;
        }
        return true;
    }

    public List<String> getValueInSlot() {
        return valueInSlot;
    }

    public String getValue() {
        return value;
    }
    
    
}
