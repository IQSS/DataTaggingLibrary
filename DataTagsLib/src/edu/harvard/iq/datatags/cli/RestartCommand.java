package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.cli.commands.CliCommand;
import edu.harvard.iq.datatags.runtime.RuntimeEngine;
import edu.harvard.iq.datatags.runtime.RuntimeEngineStatus;
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
        final RuntimeEngine engine = rnr.getEngine();
        if ( engine.getStatus() == RuntimeEngineStatus.Running ) {
            rnr.restart();
            if ( engine.getStatus() == RuntimeEngineStatus.Running ) {
                rnr.printCurrentAskNode();
            }
        } else {
            engine.setIdle();
        }
    }
    
}
