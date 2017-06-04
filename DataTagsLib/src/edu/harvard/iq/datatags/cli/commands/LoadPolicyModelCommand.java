package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.io.PolicyModelDataParser;
import edu.harvard.iq.datatags.parser.PolicyModelLoadResult;
import edu.harvard.iq.datatags.parser.PolicyModelLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Loads a policy model to the CliRunner.
 *
 * @author michael
 */
public class LoadPolicyModelCommand implements CliCommand {

    @Override
    public String command() {
        return "load";
    }

    @Override
    public String description() {
        return "Loads a policy model.";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        Path pmPath;
        String inputString;

        // get the files
        if ( args.size() == 1 ) {
            pmPath = Paths.get(args.get(0));
            
        } else {
            inputString = rnr.readLine("Please enter path to the policy model (file/folder): ").trim();
            pmPath = Paths.get(inputString);
        }
        
        if ( Files.isDirectory(pmPath) ) {
            // try to find the policy-model.xml in the folder
            pmPath = pmPath.resolve("policy-model.xml");
        }
        
        if ( ! Files.exists(pmPath) ) {
            rnr.printWarning("Policy model file '%s' not found", pmPath.toString());
        }
        
        PolicyModelDataParser pmdParser = new PolicyModelDataParser();
        PolicyModelLoadResult loadRes = PolicyModelLoader.verboseLoader()
                                                         .load(pmdParser.read(pmPath));
        
        if ( loadRes.isSuccessful() ) {
            rnr.println("Model '%s' loaded", loadRes.getModel().getMetadata().getTitle());
            rnr.setModel( loadRes.getModel() );
            rnr.restart();
        } else {
            rnr.printWarning("Failed to load model: ");
        }
        
        if ( ! loadRes.getMessages().isEmpty() ) {
            rnr.printTitle("Load Messages");
            loadRes.getMessages().forEach(m->rnr.println(m.getLevel() + "   " + m.getMessage()));
        }
    }

}
