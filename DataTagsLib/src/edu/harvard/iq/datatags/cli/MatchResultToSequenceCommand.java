package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.List;

/**
 *
 * @author michael
 */
public class MatchResultToSequenceCommand implements CliCommand {

    @Override
    public String command() {
        return "find-runs";
    }

    @Override
    public String description() {
        return "Find runs that result in the passed tags values";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        String tagValueExpression = "[>x< set: " + String.join(";", C.tail(args)) + "]";
        
        try {
            DecisionGraph dg = new DecisionGraphParser().parse(tagValueExpression).compile(rnr.getDecisionGraph().getTopLevelType());
            SetNode sn = (SetNode) dg.getNode("x");

            rnr.println( sn.toString() );
            
            
        } catch ( BadSetInstructionException bse ) {
            rnr.printWarning("Bad Set Instruction");
            bse.getBadResult().accept(new TagValueLookupResult.VoidVisitor() {
                @Override
                protected void visitImpl(TagValueLookupResult.SlotNotFound snf) {
                    rnr.printWarning("Can't find slot '" + snf.getSlotName() + "'");
                }

                @Override
                protected void visitImpl(TagValueLookupResult.ValueNotFound vnf) {
                    rnr.printWarning("Can't find value " + vnf.getValueName() + " in type " + vnf.getTagType().getName());
                }

                @Override
                protected void visitImpl(TagValueLookupResult.Ambiguity amb) {
                    rnr.printWarning("Possible results are");
                    amb.getPossibilities().forEach((poss) -> {
                        rnr.println("  " + poss);
                    });
                }

                @Override
                protected void visitImpl(TagValueLookupResult.Success scss) {
                    System.out.println("Should not have gotten here");
                    throw new RuntimeException("Set success is not a failure.");
                }
            });
        }
        
    }
    
}
