package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
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

/**
 * Main class for running decision graphs in the command line.
 * @author michael
 */
public class DecisionGraphCliRunner {
    
    public static void main(String[] args) throws Exception {
        
        if ( args.length != 2 ) {
            printUsage();
            System.exit(1);
        }
        
        Path definitionPath = Paths.get(args[0]);
        Path decisionGraphPath = Paths.get(args[1]);

        CompoundType definitions = parseDefinitions(definitionPath);
        
        DecisionGraphParser fcsParser = new DecisionGraphParser();
        
        System.out.println("Reading decision graph: " + decisionGraphPath );
        System.out.println(" (full:  " + decisionGraphPath.toAbsolutePath() + ")" );
        
        DecisionGraph dg = fcsParser.parse(decisionGraphPath).compile(definitions);
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.setDecisionGraph(dg);
        cliRunner.setTagSpacePath(definitionPath);
        cliRunner.setDecisionGraphPath(decisionGraphPath);
        
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
    
    private static void printUsage() {
        System.out.println("Usage:");
        System.out.println("DecisionGraphCliRunner <path to tagspace file> <path to decision graph file>");
    }
}
