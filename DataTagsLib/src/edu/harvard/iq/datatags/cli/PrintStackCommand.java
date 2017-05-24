package edu.harvard.iq.datatags.cli;

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
        if ( stack.isEmpty() ) {
            rnr.printMsg("Stack is empty.");
        } else {
            stack.forEach( cn -> rnr.println("[>%s< call: %s]", cn.getId(), cn.getCalleeNodeId()));
        }
                
    }
    
}
