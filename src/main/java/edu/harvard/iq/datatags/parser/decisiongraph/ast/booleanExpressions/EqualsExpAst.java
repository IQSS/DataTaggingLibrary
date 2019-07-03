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
public class EqualsExpAst extends BooleanExpressionAst {

    private List<String> valueInSlot;
    private String value;

    public List<String> getValueInSlot() {
        return valueInSlot;
    }

    public String getValue() {
        return value;
    }
    
    public EqualsExpAst(List<String> aValueInSlot, String aValue) {
        valueInSlot = aValueInSlot;
        value = aValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.valueInSlot);
        hash = 53 * hash + Objects.hashCode(this.value);
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
        final EqualsExpAst other = (EqualsExpAst) obj;
        if (!Objects.equals(this.value, other.value)) {
            return false;
        }
        if (!Objects.equals(this.valueInSlot, other.valueInSlot)) {
            return false;
        }
        return true;
    }
    
    
    
}
