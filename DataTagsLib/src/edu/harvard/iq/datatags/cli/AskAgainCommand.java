package edu.harvard.iq.datatags.cli;

import java.util.List;

/**
 * Prints the current question again.
 * @author michael
 */
public class AskAgainCommand implements CliCommand {

    @Override
    public String command() {
        return "ask";
    }

    @Override
    public String description() {
        return "Prints the current question again.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.printCurrentAskNode();
    }
    
}
