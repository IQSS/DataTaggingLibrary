package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.types.TagValueLookupResult;

/**
 *
 * @author michael
 */
public class BadSetInstructionPrinter extends TagValueLookupResult.VoidVisitor {

    public BadSetInstructionPrinter() {
    }

    @Override
    protected void visitImpl(TagValueLookupResult.SlotNotFound snf) {
        System.out.println("Can't find slot '" + snf.getSlotName() + "'");
    }

    @Override
    protected void visitImpl(TagValueLookupResult.ValueNotFound vnf) {
        System.out.println("Can't find value " + vnf.getValueName() + " in type " + vnf.getTagType().getName());
    }

    @Override
    protected void visitImpl(TagValueLookupResult.Ambiguity amb) {
        System.out.println("Possible results are");
        for (TagValueLookupResult poss : amb.getPossibilities()) {
            System.out.println("  " + poss);
        }
    }

    @Override
    protected void visitImpl(TagValueLookupResult.Success scss) {
        System.out.println("Should not have gotten here");
        throw new RuntimeException("Set success is not a failure.");
    }
    
}
