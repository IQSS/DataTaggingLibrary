package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import java.util.List;

/**
 * Prints the current tags to the console.
 * @author michael
 */
public class ShowCurrentValueCommand implements CliCommand {

    @Override
    public String command() {
        return "current-value";
    }

    @Override
    public String description() {
        return "Print the current value to the console.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.dumpTagValue( rnr.getEngine().getCurrentValue() );
        rnr.println("");
    }
    
}
