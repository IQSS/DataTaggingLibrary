package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import java.util.List;

/**
 *
 * @author michael
 */
public class ToggleDebugMessagesCommand implements CliCommand {

    @Override
    public String command() {
        return "debug";
    }

    @Override
    public String description() {
        return "Toggles printing of debug messages.";
    }

    @Override
    public boolean requiresModel() {
        return false;
    }
    
    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if ( args.size() > 1 ) {
            switch ( args.get(1).toLowerCase().trim() ) {
                case "on" :
                case "1" :
                case "true" :
                case "yes" :
                    rnr.setPrintDebugMessages(true);
                    break;
                case "off":
                case "0":
                case "false":
                case "no":
                    rnr.setPrintDebugMessages(false);
                    break;
                default:
                    rnr.printWarning("Unknown parameter %s. Please use on/off.", args.get(1));
            }
        }
        rnr.printMsg( "Debug messages are [%s]%s" , 
                toParamStr(rnr.getPrintDebugMessages()),
                args.size()==1 ? (" (use `debug " + toParamStr(rnr.getPrintDebugMessages()) + "` to change.)") : ""
                );
    }
    
    private String toParamStr( boolean b ) {
        return b?"on":"off";
    }
}
