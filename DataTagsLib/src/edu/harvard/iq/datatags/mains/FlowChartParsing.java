package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.parser.decisiongraph.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstNode;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeRefVizalizer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * Main class for parsing a flowchart file.
 *
 * @author Michael Bar-Sinai
 */
public class FlowChartParsing {

    public static void main(String[] args) throws IOException {
        Path inputFile = Paths.get(args[0]);
        
        System.out.println("Reading " + inputFile );
        System.out.println(" (full:  " + inputFile.toAbsolutePath() + ")" );
        
        String source = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);
        
        FlowChartASTParser astParser = new FlowChartASTParser();
        
        List<AstNode> refs = astParser.graphParser().parse(source);
        GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
        Path outfile = inputFile.resolveSibling( inputFile.getFileName().toString() + ".gv" );
        System.out.println("Writing: " + outfile );
        viz.vizualize( outfile );

    }
}
