package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeRefVizalizer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 *
 * @author michael
 */
public class FlowChartCompiling {
	public static void main(String[] args) throws IOException {
        Path inputFile = Paths.get(args[0]);
        
        System.out.println("Reading " + inputFile );
        System.out.println(" (full:  " + inputFile.toAbsolutePath() + ")" );
        
        String source = new String(Files.readAllBytes(inputFile), StandardCharsets.UTF_8);
        
        FlowChartSetComplier fcsParser = new FlowChartSetComplier();
        FlowChartASTParser astParser = new FlowChartASTParser();
        
        List<InstructionNodeRef> refs = astParser.graphParser().parse(source);
        GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
        Path outfile = inputFile.resolveSibling( inputFile.getFileName().toString() + "-ast.gv" );
        System.out.println("Writing: " + outfile );
        viz.vizualize( outfile );

        FlowChartSet fcs = fcsParser.parse(refs, inputFile.getFileName().toString());
		
        GraphvizChartSetVisualizer fcsViz = new GraphvizChartSetVisualizer();
        outfile = inputFile.resolveSibling( inputFile.getFileName().toString() + "-fcs.gv" );
        System.out.println("Writing: " + outfile );
		fcsViz.setChartSet(fcs);
        fcsViz.vizualize( outfile );

    }
}
