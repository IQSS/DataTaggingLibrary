package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.BriefNodePrinter;
import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.model.decisiongraph.nodes.ThroughNode;
import java.util.Deque;
import java.util.List;

/**
 * Prints the current stack of the engine.
 * @author michael
 */
public class PrintStackCommand implements CliCommand {

    @Override
    public String command() {
        return "stack";
    }

    @Override
    public String description() {
        return "Prints the current stack of the engine.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        final Deque<ThroughNode> stack = rnr.getEngine().getStack();
        BriefNodePrinter BNP = new BriefNodePrinter(rnr);
        if ( stack.isEmpty() ) {
            rnr.printMsg("Stack is empty.");
        } else {
//            stack.forEach( cn -> rnr.println("[>%s< call: %s]", cn.getId(), cn.getCalleeNodeId()));
              stack.forEach(cn -> cn.accept(BNP));
        }
                
    }
    
}
