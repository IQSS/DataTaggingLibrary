package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.BriefNodePrinter;
import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.ThroughNode;
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
