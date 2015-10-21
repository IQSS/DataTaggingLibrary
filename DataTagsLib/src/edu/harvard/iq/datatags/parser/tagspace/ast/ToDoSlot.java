package edu.harvard.iq.datatags.parser.tagspace.ast;

/**
 * A slot that is declared now, but not fulfilled yet. Can be referenced by other slots.
 * This mechanism allows for top-down development.
 * 
 * {@code 
 * Biggy: consists of SlotA, SlotB.
 * SlotA: one of AA, AB.
 * SlotB: TODO.
 * SlotC [We'll need to implement this... but how?]: TODO.
 * 
 * }
 * @author michael
 */
public class ToDoSlot extends AbstractSlot {
    
    public ToDoSlot(String aName, String aNote) {
        super(aName, aNote);
    }
    
    @Override
    public <R> R accept(AbstractSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
    
}
