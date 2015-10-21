package edu.harvard.iq.datatags.cli;

/**
 *
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
    public void execute(CliRunner rnr) throws Exception {
        System.exit(0);
    }
    
}
