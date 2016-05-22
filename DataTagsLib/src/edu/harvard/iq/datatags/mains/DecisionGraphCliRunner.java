package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.LoadQuestionnaireCommand;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;

/**
 * Main class for running decision graphs in the command line.
 * @author michael
 */
public class DecisionGraphCliRunner {
    
    public static void main(String[] args) throws Exception {
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.printSplashScreen();
        
        if ( args.length < 2 ) {
            while ( cliRunner.getDecisionGraph() == null ) {
                // we need a loop here since loading the questionnaire may fail.
                new LoadQuestionnaireCommand().execute(cliRunner, Collections.emptyList());
            }
            
        } else {
            Path tagSpace = Paths.get(args[args.length-2]);
            if ( ! Files.exists(tagSpace) ) {
                cliRunner.printWarning("File %s not found", tagSpace.toString());
                System.exit(1);
            }
            
            Path decisionGraphPath = Paths.get(args[args.length-1]);
            if ( ! Files.exists(decisionGraphPath) ) {
               cliRunner.printWarning("File %s not found", tagSpace.toString());
               System.exit(2);
            }

            CompoundType definitions = parseDefinitions(tagSpace);

            DecisionGraphParser fcsParser = new DecisionGraphParser();

            System.out.println("Reading decision graph: " + decisionGraphPath );
            System.out.println(" (full:  " + decisionGraphPath.toAbsolutePath() + ")" );
            
            try {
                DecisionGraph dg = fcsParser.parse(decisionGraphPath).compile(definitions);
                cliRunner.setDecisionGraph(dg);
                cliRunner.setTagSpacePath(tagSpace);
                cliRunner.setDecisionGraphPath(decisionGraphPath);
                
            } catch ( DataTagsParseException dte ) {
                cliRunner.printWarning("Error loading questionnaire: ");
                cliRunner.printWarning(dte.getMessage());
                if ( dte.getOffendingNode() != null ) {
                    cliRunner.printWarning("Offending node: %s", dte.getOffendingNode());
                }
                if ( dte.getWhere()!=null ) {
                    cliRunner.printWarning("At location: %s", dte.getWhere());
                }
                System.exit(2);
            }
        }
        cliRunner.go();
        
    }
    
    public static CompoundType parseDefinitions(Path definitionsFile) throws DataTagsParseException, IOException {
        
        System.out.println("Reading definitions: " + definitionsFile );
        System.out.println(" (full:  " + definitionsFile.toAbsolutePath() + ")" );
        
        return new TagSpaceParser().parse(readAll(definitionsFile)).buildType("DataTags").get();
    }
    
    private static String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
    
}
