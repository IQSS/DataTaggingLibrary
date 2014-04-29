    package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
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

        CompoundType definitions = (CompoundType) parseDefinitions(definitionFile);
        
        FlowChartSetComplier fcsParser = new FlowChartSetComplier( definitions );
        
        System.out.println("Reading chart: " + chartFile );
        System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")" );
        
        String source = readAll(chartFile);
        
        FlowChartSet fcs = fcsParser.parse(source, chartFile.getFileName().toString() );
        
        CliRunner cliRunner = new CliRunner();
        cliRunner.setFcs(fcs);
        cliRunner.go();
        
    }
    
    public static TagType parseDefinitions(Path definitionsFile) throws DataTagsParseException, IOException {
        
        System.out.println("Reading definitions: " + definitionsFile );
        System.out.println(" (full:  " + definitionsFile.toAbsolutePath() + ")" );
        
        return new  DataDefinitionParser().parseTagDefinitions(readAll(definitionsFile), definitionsFile.getFileName().toString());
    }
    
    private static String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

}
