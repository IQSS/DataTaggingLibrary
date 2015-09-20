package edu.harvard.iq.datatags.parser.definitions.ast;

/**
 * A slot that is declared now, but not fulfilled yet. Can be referenced by other slots.
 * This mechanism allows for top-down development.
 * 
 * {@code 
 * Biggy: consists of SlotA, SlotB.
 * SlotA: one of AA, AB.
 * SlotB: TODO.
 * SlotC [We'll need to push this guy somewhere.... but where?]: TODO.
 * 
 * }
 * @author michael
 */
public class TodoSlot extends AbstractSlot {
    
    public TodoSlot(String aName, String aNote) {
        super(aName, aNote);
    }
    
    @Override
    public <R> R accept(AbstractSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
    
}
