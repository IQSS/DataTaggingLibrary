package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import java.util.List;

/**
 * Restarts the engine.
 * @author michael
 */
public class RestartCommand implements CliCommand {

    @Override
    public String command() {
        return "restart";
    }

    @Override
    public String description() {
        return "Restarts the interview.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.restart();
    }
    
}
