package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import java.util.List;

/**
 * Quits The command line runner.
 * @author michael
 */
public class QuitCommand implements CliCommand {

    @Override
    public String command() {
        return "quit";
    }

    @Override
    public String description() {
        return "Terminates the interview.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        System.exit(0);
    }
    
    @Override
    public boolean requiresModel() {
        return false;
    }
    
}
