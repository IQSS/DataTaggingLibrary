package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.tools.queries.DecisionGraphQuery;
import edu.harvard.iq.datatags.tools.queries.FindSupertypeResultsDgq;
import edu.harvard.iq.datatags.tools.queries.RunTrace;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Iterator;
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
        String tagValueExpression = "[>x< set: " + String.join(" ", C.tail(args)) + "]";
        rnr.debugPrint( tagValueExpression );
        try {
            DecisionGraph dg = new DecisionGraphParser().parse(tagValueExpression).compile(rnr.getDecisionGraph().getTopLevelType());
            SetNode sn = (SetNode) dg.getNode("x");

            FindSupertypeResultsDgq query = new FindSupertypeResultsDgq(rnr.getDecisionGraph(), sn.getTags());
            
            query.get( new DecisionGraphQuery.Listener() {
                long foundCount = 0;
                long missCount = 0;
                @Override
                public void matchFound(DecisionGraphQuery dgq) {
                    foundCount++;
                    rnr.println("Run %d:",foundCount);
                    printRunTrace(rnr, dgq.getCurrentTrace()) ;
                    rnr.println();
                }

                @Override
                public void nonMatchFound(DecisionGraphQuery dgq) {
                    missCount++;
                    if ( missCount%1000==0 ) {
                        rnr.println("%d runs inspected", missCount);
                    }
                }

                @Override
                public void started(DecisionGraphQuery dgq) { }

                @Override
                public void done(DecisionGraphQuery dgq) {
                    rnr.println("Found %,d matches in %,d possible runs.", foundCount, missCount+foundCount);
                }
            });
            
            
            
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

                @Override
                protected void visitImpl(TagValueLookupResult.SyntaxError serr) {
                   rnr.printWarning("Syntax Error: %s\n  %s", serr.getExpression(), serr.getHint());
                }
            });
        } catch ( DataTagsParseException dpe ) {
            rnr.printWarning( dpe.getMessage() );
        }
        
    }

    private void printRunTrace(CliRunner rnr, RunTrace t) {
        Iterator<Node> nodes = t.getNodes().iterator();
        Iterator<Answer> answers = t.getAnswers().iterator();
        BriefNodePrinter nodePrinter = new BriefNodePrinter(rnr);
        
        while ( nodes.hasNext() ) {
            Node nd = nodes.next();
            nd.accept(nodePrinter);
            if ( nd instanceof AskNode ) {
                rnr.println(" -%s->", answers.next().getAnswerText());
            }
        }
        
        if ( t.getValue()!=null ) {
            rnr.println("Final Tags:");
            rnr.dumpTagValue( t.getValue() );
        } else {
            rnr.println( "(no final tags)" );
        }
    }
    
}
