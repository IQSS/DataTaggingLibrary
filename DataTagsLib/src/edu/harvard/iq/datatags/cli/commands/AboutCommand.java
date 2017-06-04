package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.PolicyModel;
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
        rnr.println( CliRunner.LOGO );
        rnr.println("");
        rnr.printTitle("About DataTags CliRunner");
        rnr.println("This application runs DataTags decision graphs from the a command line.");
        rnr.println("The user is asked questions about a dataset, and based on her answers,");
        rnr.println("a set of DataTags are created. These tags can be used to create a proper");
        rnr.println("data handling policy for said dataset.");
        rnr.println("");
        rnr.println("For more info: http://datatags.org");
        rnr.println("");
        
        PolicyModel dg = rnr.getEngine().getModel();
        if ( dg != null ) {
            rnr.printTitle("Current Model");
            rnr.println("title: %s", rnr.getModel().getMetadata().getTitle() );
            rnr.println("   at: %s", rnr.getModel().getMetadata().getMetadataFile().getParent() );
            
        }
        rnr.println("Engine status: %s", rnr.getEngine().getStatus());
        
    }
    
}
