package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.LoadPolicyModelCommand;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
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
            new LoadPolicyModelCommand().execute(cliRunner, Arrays.asList(args[0]));
            
            if ( cliRunner.getModel() == null ) {
                cliRunner.printWarning(String.format("Loading model '%s' failed.", args[0]));
                System.exit(2);
            }
        }
        cliRunner.go();
        
    }
    
    public static CompoundSlot parseDefinitions(Path definitionsFile) throws DataTagsParseException, IOException {

        System.out.println("Reading definitions: " + definitionsFile );
        System.out.println(" (full:  " + definitionsFile.toAbsolutePath() + ")" );
        
        return new TagSpaceParser().parse(readAll(definitionsFile)).buildType("DataTags").get();
    }
    
    private static String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    
}
