package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
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
        return "Quits this application. Terminates the interview, if one is currently running.";
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
