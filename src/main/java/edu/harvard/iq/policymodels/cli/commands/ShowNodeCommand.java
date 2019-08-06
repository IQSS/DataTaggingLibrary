package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.cli.VerboseNodePrinter;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.Node;
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
        return "Prints a node to the console";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if ( args.size() < 2 ) {
            rnr.printWarning("Please supply node id.");
            return;
        }
        Node soughtNode = rnr.getModel().getDecisionGraph().getNode(args.get(1));
        if ( soughtNode == null ) {
            rnr.printWarning("Node >%s< not found.", args.get(1));
        } else {
            soughtNode.accept(new VerboseNodePrinter(rnr));
        }
        
    }

    
}
