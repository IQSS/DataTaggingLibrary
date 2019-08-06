package edu.harvard.iq.policymodels.cli;

import edu.harvard.iq.policymodels.model.policyspace.slots.SlotValueLookupResult;

/**
 *
 * @author michael
 */
public class BadSetInstructionPrinter extends SlotValueLookupResult.VoidVisitor {

    public BadSetInstructionPrinter() {
    }

    @Override
    protected void visitImpl(SlotValueLookupResult.SlotNotFound snf) {
        System.out.println("Can't find slot '" + snf.getSlotName() + "'");
    }

    @Override
    protected void visitImpl(SlotValueLookupResult.ValueNotFound vnf) {
        System.out.println("Can't find value " + vnf.getValueName() + " in type " + vnf.getTagType().getName());
    }

    @Override
    protected void visitImpl(SlotValueLookupResult.Ambiguity amb) {
        System.out.println("Possible results are");
        for (SlotValueLookupResult poss : amb.getPossibilities()) {
            System.out.println("  " + poss);
        }
    }

    @Override
    protected void visitImpl(SlotValueLookupResult.Success scss) {
        System.out.println("Should not have gotten here");
        throw new RuntimeException("Set success is not a failure.");
    }
    
}
