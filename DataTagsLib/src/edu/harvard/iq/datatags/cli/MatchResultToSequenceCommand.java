package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.tools.queries.FindSupertypeResultsDgq;
import edu.harvard.iq.datatags.tools.queries.RunTrace;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

            FindSupertypeResultsDgq query = new FindSupertypeResultsDgq(rnr.getDecisionGraph(), sn.getTags());
            
            Set<RunTrace> traces = query.get();
            if ( traces.isEmpty() ) {
                rnr.println("No matching runs found.");
            } else {
                rnr.println("%d Runs found", traces.size());
                AtomicInteger ser = new AtomicInteger();
                traces.forEach( t -> {
                    rnr.println("Run %d:", ser.incrementAndGet());
                    printRunTrace(rnr, t) ;
                    rnr.println();
                });
            }
            
            
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
        
        rnr.println("Final Tags:");
        rnr.println( Objects.toString(t.getValue(), "(no final tags)"));
    }
    
    
}
