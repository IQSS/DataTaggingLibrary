/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import java.util.List;

/**
 *
 * @author mor
 */
public abstract class SlotValuePair {
    
    public interface Visitor {

        void visit(AtomicSlotValuePair aa);

        void visit(AggregateSlotValuePair aa);
    }
    final List<String> slot;

    public SlotValuePair(List<String> aSlot) {
        slot = aSlot;
    }

    public List<String> getSlot() {
        return slot;
    }

    public abstract void accept(Visitor v);
    
}


