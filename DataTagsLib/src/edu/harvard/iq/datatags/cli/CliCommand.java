package edu.harvard.iq.datatags.cli;

/**
 * A command that can be invoked from the commandline, while running in a {@link CliRunner}.
 * @author michael
 */
public interface CliCommand {
    
    /**
     * @return the command name (e.g {@code current-tags}.
     */
    String command();
    
    /**
     * @return a short description of the command.
     */
    String description();
    
    /**
     * Actually do stuff.
     * @param rnr the runner on which we operate.
     * @throws Exception 
     */
    void execute( CliRunner rnr ) throws Exception;
}
