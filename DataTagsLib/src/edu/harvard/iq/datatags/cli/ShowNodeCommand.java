package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.List;

/**
 * Dumps a node to the console.
 * @author michael
 */
public class ShowNodeCommand implements CliCommand {

    @Override
    public String command() {
        return "show-node";
    }

    @Override
    public String description() {
        return "Prints a description of the node whose ID is given.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if ( args.size() < 2 ) {
            rnr.printWarning("Please supply node id.");
        }
        Node soughtNode = rnr.getDecisionGraph().getNode(args.get(1));
        if ( soughtNode == null ) {
            rnr.printWarning("Node >%s< not found.", args.get(1));
        } else {
            soughtNode.accept( new Node.VoidVisitor() {
                @Override
                public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [ask]", nd.getId());
                    rnr.println( nd.getText() );
                    if ( !nd.getTermNames().isEmpty() ) {
                        rnr.println("Terms:");
                        nd.getTermNames().forEach( (term) -> {
                            rnr.println( term + ":" );
                            rnr.println( "\t" + nd.getTermText(term));
                        });
                    }
                    rnr.println( "Answers: " );
                    nd.getAnswers().forEach( a -> {
                        rnr.print( a.getAnswerText() );
                        rnr.println( " -> >%s<", nd.getNodeFor(a).getId() );
                    });

                }

                @Override
                public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [set]", nd.getId());
                    rnr.dumpTagValue(nd.getTags());
                }

                @Override
                public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [reject]", nd.getId());
                    rnr.print( nd.getReason() );
                }

                @Override
                public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [call]", nd.getId());
                    rnr.print( "Calls node >%s<.", nd.getCalleeNodeId() );
                }

                @Override
                public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [todo]", nd.getId());
                    rnr.print( nd.getTodoText() );
                }

                @Override
                public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
                    rnr.printTitle("Node >%s<: [end]", nd.getId());
                }
            });
        }
        
    }
    
}
