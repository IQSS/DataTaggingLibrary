package edu.harvard.iq.policymodels.parser.policyspace.ast;

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
public class ToDoAstSlot extends AbstractAstSlot {
    
    public ToDoAstSlot(String aName, String aNote) {
        super(aName, aNote);
    }
    
    @Override
    public <R> R accept(AbstractAstSlot.Visitor<R> visitor ) {
        return visitor.visit(this);
    }
    
}
