package edu.harvard.iq.datatags.cli;

import java.util.List;

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
     * @param args Method arguments supplied by the user. Similar to C/Java, argument 0
     *             is the name of the command, and the rest of the arguments are the 
     *             command parameters.
     * @throws Exception 
     */
    void execute( CliRunner rnr, List<String> args) throws Exception;
}
