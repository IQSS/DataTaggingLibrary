package edu.harvard.iq.policymodels.cli.commands;

import java.util.List;
import static java.util.stream.Collectors.toList;

/**
 * Base class for commands. Mainly for sharing useful code.
 *
 * @author michael
 */
public abstract class AbstractCliCommand implements CliCommand {

    private final String commandName;
    private final String description;

    protected AbstractCliCommand(String commandName, String Description) {
        this.commandName = commandName;
        this.description = Description;
    }

    @Override
    public String command() {
        return commandName;
    }

    @Override
    public String description() {
        return description;
    }

    protected List<String> noFlags(List<String> args ) {
        return args.stream().filter(a -> !isFlag(a)).collect(toList());
    }
    
    protected boolean hasFlag(String flagName, List<String> args) {
        return args.stream().anyMatch(arg -> arg.trim().equals("-" + flagName));
    }

    protected boolean isFlag(String aString) {
        return aString.trim().startsWith("-");
    }

}
