package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.commands.LoadPolicyModelCommand;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;

/**
 * Main class for running decision graphs in the command line.
 * @author michael
 */
public class PolicyModelCliRunner {
    
    public static void main(String[] args) throws Exception {
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.printSplashScreen();
        
        if ( args.length ==0 ) {
            while ( cliRunner.getModel() == null ) {
                // we need a loop here since loading the questionnaire may fail.
                new LoadPolicyModelCommand().execute(cliRunner, Collections.emptyList());
            }
            
        } else {
            Path modelPath = Paths.get(args[0]);
            if ( ! Files.exists(modelPath) ) {
                cliRunner.printWarning("File %s not found", modelPath.toString());
                System.exit(1);
            }
            System.out.println("Loading model from " + modelPath);
            new LoadPolicyModelCommand().execute(cliRunner, Arrays.asList("dummy", args[0]));
            
            if ( cliRunner.getModel() == null ) {
                cliRunner.printWarning(String.format("Loading model '%s' failed.", args[0]));
                System.exit(2);
            }
        }
        cliRunner.go();
        
    }
    
}
