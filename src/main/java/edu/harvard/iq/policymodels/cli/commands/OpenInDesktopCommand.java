package edu.harvard.iq.policymodels.cli.commands;

import edu.harvard.iq.policymodels.cli.CliRunner;
import java.awt.Desktop;
import java.util.List;

/**
 *
 * @author michael
 */
public class OpenInDesktopCommand extends AbstractCliCommand {

    public OpenInDesktopCommand() {
        super("show", "Open the current model using the system's file browser.");
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        if ( Desktop.isDesktopSupported() ) {
            Desktop desktop = Desktop.getDesktop();
            desktop.open(rnr.getModel().getMetadata().getMetadataFile().getParent().toFile());
            
        } else {
            rnr.printWarning("Sorry, this operating system does not support the required functionality.");
        }
    }
    
}
