package edu.harvard.iq.datatags.cli;

import java.util.List;

/**
 *
 * @author michael
 */
public class ToggleDebugMessagesCommand implements CliCommand {

    @Override
    public String command() {
        return "debug-messages";
    }

    @Override
    public String description() {
        return "Toggles printing of debug messages.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.setPrintDebugMessages(! rnr.getPrintDebugMessages() );
        rnr.printMsg("Debug messages %s", (rnr.getPrintDebugMessages() ? "On" : "Off"));
    }
    
}
