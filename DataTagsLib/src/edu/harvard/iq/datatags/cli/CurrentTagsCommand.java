package edu.harvard.iq.datatags.cli;

import java.util.List;

/**
 * Prints the current tags to the console.
 * @author michael
 */
public class CurrentTagsCommand implements CliCommand {

    @Override
    public String command() {
        return "current-tags";
    }

    @Override
    public String description() {
        return "Print the current tags to the console.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.dumpTagValue( rnr.getEngine().getCurrentTags() );
        rnr.println("");
    }
    
}
