package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
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
    public boolean requiresModel() {
        return false;
    }
    
    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        rnr.println( CliRunner.LOGO );
        rnr.println("");
        rnr.printTitle("About PolicyModels CliRunner");
        rnr.println("This application allows working with policy models from the a command line.");
        rnr.println("");
        rnr.println("For more info: http://datatags.org");
        rnr.println("");
        
        PolicyModel model = rnr.getEngine().getModel();
        if ( model != null ) {
            rnr.printTitle("Current Model");
            PolicyModelData metadata = model.getMetadata();
            rnr.println("  title: %s", metadata.getTitle() );
            rnr.println("version: %s", metadata.getVersion() );
            rnr.println("     at: %s", metadata.getMetadataFile().getParent() );
            if ( ! model.getLocalizations().isEmpty() ) {
                rnr.println("Localizations:");
                model.getLocalizations().forEach(lName -> rnr.println("* " + lName));
            }
        }
        rnr.println("Engine status: %s", rnr.getEngine().getStatus());
        
    }
    
}
