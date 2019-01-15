package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.BriefNodePrinter;
import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.graphs.Answer;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.tools.queries.DecisionGraphQuery;
import edu.harvard.iq.datatags.tools.queries.FindSupertypeResultsDgq;
import edu.harvard.iq.datatags.tools.queries.RunTrace;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * This is the command behind "find-runs". It outputs all the paths
 * of an interview, that result in tag values that are a superset of the 
 * value query.
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
            CompilationUnit cu = new CompilationUnit(tagValueExpression);
            try {
                cu.compile(rnr.getModel().getSpaceRoot(), new EndNode("SYN-END"), new ArrayList<>());
            } catch ( RuntimeException rte ) {
                if ( rte.getCause() instanceof BadSetInstructionException ) {
                    throw (BadSetInstructionException) rte.getCause();
                } else {
                    throw rte;
                }
            }
            SetNode sn = (SetNode) cu.getDecisionGraph().getNode("x");
            System.out.println(sn.getTags());
            
            FindSupertypeResultsDgq query = new FindSupertypeResultsDgq(rnr.getModel(), sn.getTags());

            query.get( new DecisionGraphQuery.Listener() {
                long foundCount = 0;
                long missCount = 0;
                long rejectCount = 0;
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
                    if ( totalRunsInspected()%1000==0 ) {
                        rnr.println(" - So far: %,d matches, %,d misses, %,d rejects", foundCount, missCount, rejectCount);
                    }
                }
                
                @Override
                public void rejectionFound(DecisionGraphQuery dgq) {
                    rejectCount++;
                    if ( totalRunsInspected()%1000==0 ) {
                        rnr.println(" - So far: %,d matches, %,d misses, %,d rejects", foundCount, missCount, rejectCount);
                    }
                }

                @Override
                public void started(DecisionGraphQuery dgq) {
                    rnr.println("started");
                }

                @Override
                public void done(DecisionGraphQuery dgq) {
                    switch ((int)foundCount) {
                        case 0:
                            rnr.println("No matching runs found, out of %,d possible runs.", missCount);
                            break;
                        case 1:
                            rnr.println("Found a single match %,d possible runs.", missCount+foundCount);
                            break;
                        default:
                            rnr.println("Found %,d matches in %,d possible runs.", foundCount, missCount+foundCount);
                            break;
                    }
                }

                @Override
                public void loopDetected(DecisionGraphQuery dgq) {
                    rnr.println("Loop detected with this trace - ");
                    printRunTrace(rnr, dgq.getCurrentTrace()) ;
                }
                
                private long totalRunsInspected() {
                    return foundCount + missCount + rejectCount;
                }
            });



        } catch ( BadSetInstructionException bse ) {
            if ( bse.getBadLookupException() != null ) {
                rnr.printWarning( bse.getBadLookupException().getMessage() );
            } else {
                rnr.printWarning("Error parsing value expression. Is the slot name correct?");
            }
        } catch ( DataTagsParseException dpe ) {
            rnr.printWarning( dpe.getMessage() );
            rnr.printWarning( "Was the value or slot names entered legal? (i.e. start with a letter, no spaces, etc.)" );
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
