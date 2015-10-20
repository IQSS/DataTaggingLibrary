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
 *
 * @author michael
 */
public class FlowChartExecuteCli {
    
    public static void main(String[] args) throws Exception {
        Path definitionFile = Paths.get(args[0]);
        Path chartFile = Paths.get(args[1]);

        CompoundType definitions = parseDefinitions(definitionFile);
        
        DecisionGraphParser fcsParser = new DecisionGraphParser();
        
        System.out.println("Reading chart: " + chartFile );
        System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")" );
        
        String source = readAll(chartFile);
        
        DecisionGraph dg = fcsParser.parse(source).compile(definitions);
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.setDecisionGraph(dg);
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
