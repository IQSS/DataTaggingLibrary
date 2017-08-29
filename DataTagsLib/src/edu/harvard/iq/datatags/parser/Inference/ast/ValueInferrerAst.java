/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.Inference.ast;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode.Assignment;
import edu.harvard.iq.datatags.parser.tagspace.ast.CompoundAstSlot;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferrerAst {
    
    public static class InferencePairAst {
        private final List<Assignment> minimalCoordinate;
        private final String inferredValue;

        public InferencePairAst(List<Assignment> minimalCoordinate, String inferredValue) {
            this.minimalCoordinate = minimalCoordinate;
            this.inferredValue = inferredValue;
        }

        public List<Assignment> getMinimalCoordinate() {
            return minimalCoordinate;
        }

        public String getInferredValue() {
            return inferredValue;
        }

        @Override
        public int hashCode() {
            int hash = 5;
            hash = 79 * hash + Objects.hashCode(this.minimalCoordinate);
            hash = 79 * hash + Objects.hashCode(this.inferredValue);
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
            final InferencePairAst other = (InferencePairAst) obj;
            if (!Objects.equals(this.inferredValue, other.inferredValue)) {
                return false;
            }
            if (!Objects.equals(this.minimalCoordinate, other.minimalCoordinate)) {
                return false;
            }
            return true;
        }

        
        
    }
    
    List<String> slot;
    List<InferencePairAst> inferencePair;

    public ValueInferrerAst(List<String> slot, List<InferencePairAst> inferencePair) {
        this.slot = slot;
        this.inferencePair = inferencePair;
    }
    
    public List<String> getSlot() {
        return slot;
    }

    public List<InferencePairAst> getInferencePairs() {
        return inferencePair;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.slot);
        hash = 79 * hash + Objects.hashCode(this.inferencePair);
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
        final ValueInferrerAst other = (ValueInferrerAst) obj;
        if (!Objects.equals(this.slot, other.slot)) {
            return false;
        }
        if (!Objects.equals(this.inferencePair, other.inferencePair)) {
            return false;
        }
        return true;
    }
    
    
    
    public String toStringSlotName(){
        return String.join("/", slot);
    }
    
    
}
