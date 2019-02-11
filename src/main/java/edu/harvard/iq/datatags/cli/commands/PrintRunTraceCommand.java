package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.BriefNodePrinter;
import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.VerboseNodePrinter;
import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.List;

/**
 * Prints the trace of the current run.
 * @author michael
 */
public class PrintRunTraceCommand implements CliCommand {

    @Override
    public String command() {
        return "trace";
    }

    @Override
    public String description() {
        return "Print the trace of the current run - all the nodes visited, in order.\n"
                + "Use 'trace -v' for a more verbose printout.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        Node.Visitor printer = (C.last(args).equals("-v") ? new VerboseNodePrinter(rnr) : new BriefNodePrinter(rnr));
        rnr.getTrace().forEach( n -> n.accept(printer) );
    }
    
}
