package edu.harvard.iq.datatags.cli;

import java.util.List;

/**
 *
 * @author michael
 */
public class AboutCommand implements CliCommand {

    @Override
    public String command() {
        return "about";
    }

    @Override
    public String description() {
        return "What's this application all about.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.println("");
        rnr.printTitle("About DataTags CliRunner");
        rnr.println("This application runs DataTags decision graphs from the a command line.");
        rnr.println("The user is asked questions about a dataset, and based on her answers,");
        rnr.println("a set of DataTags are created. These tags can be used to create a proper");
        rnr.println("data handling policy for said dataset.");
        rnr.println("");
        rnr.println("For more info: http://datatags.org");
        rnr.println("");
    }
    
}
