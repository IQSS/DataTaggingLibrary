package edu.harvard.iq.policymodels.mains;

import edu.harvard.iq.policymodels.PolicyModelsInfo;
import edu.harvard.iq.policymodels.cli.CliRunner;
import edu.harvard.iq.policymodels.cli.commands.LoadPolicyModelCommand;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * Main class for running decision graphs in the command line.
 * @author michael
 */
public class PolicyModelCliRunner {
    
    public static void main(String[] args) throws Exception {
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.printSplashScreen();
        cliRunner.println("PolicyModels library version: " + PolicyModelsInfo.getVersionString() );
        
        if ( args.length > 0 ) {
            Path modelPath = Paths.get(args[0]);
            if ( ! Files.exists(modelPath) ) {
                cliRunner.printWarning("File %s not found", modelPath.toString());
                System.exit(1);
            }
            cliRunner.println("Loading model from " + modelPath);
            new LoadPolicyModelCommand().execute(cliRunner, Arrays.asList("dummy", args[0]));
            
            if ( cliRunner.getModel() == null ) {
                cliRunner.printWarning(String.format("Loading model '%s' failed.", args[0]));
            }
        }
        cliRunner.go();
        
    }
    
}
