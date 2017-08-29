/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.values.CompoundValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferrer{
    List<InferencePair> inferencePairs;

    public static class InferencePair {

        CompoundValue minimalCoordinate;
        CompoundValue inferredValue;

        public InferencePair(CompoundValue minimalCoordinate, CompoundValue inferredValue) {
            this.minimalCoordinate = minimalCoordinate;
            this.inferredValue = inferredValue;
        }

        public CompoundValue getMinimalCoordinate() {
            return minimalCoordinate;
        }
        
        public CompoundValue getInferredValue() {
            return inferredValue;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.minimalCoordinate);
            hash = 97 * hash + Objects.hashCode(this.inferredValue);
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
            final InferencePair other = (InferencePair) obj;
            if (!Objects.equals(this.minimalCoordinate, other.minimalCoordinate)) {
                return false;
            }
            if (!Objects.equals(this.inferredValue, other.inferredValue)) {
                return false;
            }
            return true;
        }
        
        
    }

    public ValueInferrer() {
        inferencePairs = new ArrayList<>();
    }

    public boolean add(InferencePair e) {
        return inferencePairs.add(e);
    }

    public List<InferencePair> getInferencePairs() {
        return inferencePairs;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(this.inferencePairs);
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
        final ValueInferrer other = (ValueInferrer) obj;
        if (!Objects.equals(this.inferencePairs, other.inferencePairs)) {
            return false;
        }
        return true;
    }

    
    
    
}
