package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParseResult;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetClusteredVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDataStructureVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeAstVizalizer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * @author michael
 */
public class DecisionGraphCompiling {
    public static void main(String[] args) {
        DecisionGraphCompiling fcc = new DecisionGraphCompiling();
        try {
            fcc.go(args);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage() );
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
            
        } catch (BadSetInstructionException ex) {
            TagValueLookupResult badRes = ex.getBadResult();
            
            if ( badRes != null ) {
                System.out.println("Bad Set instruction: " + ex.getMessage());
                badRes.accept( new BadSetInstructionPrinter() );
            }
            System.out.println("Error in [set] node: " + ex.getMessage() );
            System.out.println("offending Set node: " + ex.getOffendingNode() );
            
        } catch (DataTagsParseException ex) {
            System.out.println("Semantic Error in data tags program: " + ex.getMessage() );
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
        }
    }
    
	public void go(String[] args) throws IOException, DataTagsParseException {
        Path tagsFile = Paths.get(args[0]);
        Path chartFile = Paths.get(args[1]);
        
        System.out.println("Reading tags: " + tagsFile );
        System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")" );
        
        TagSpaceParser tagsParser = new TagSpaceParser();
        CompoundType baseType = tagsParser.parse(readAll(tagsFile)).buildType("DataTags").get();
        
        GraphvizDataStructureVisualizer tagViz = new GraphvizDataStructureVisualizer(baseType);
        Path tagsOutPath = tagsFile.resolveSibling(tagsFile.getFileName().toString() + ".gv");
        System.out.println("Writing " + tagsOutPath );
		tagViz.vizualize( tagsOutPath );
        
        System.out.println("Reading chart: " + chartFile );
        System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")" );
        
        String source = readAll(chartFile);
        
        DecisionGraphParser dgParser = new DecisionGraphParser();
        DecisionGraphParseResult parsedGraph = dgParser.parse(source);
        
        GraphvizGraphNodeAstVizalizer viz = new GraphvizGraphNodeAstVizalizer(parsedGraph.getNodes());
        Path outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-ast.gv" );
        System.out.println("Writing: " + outfile );
        viz.vizualize( outfile );
        
        DecisionGraph dg = parsedGraph.compile(baseType);
		
        GraphvizChartSetClusteredVisualizer fcsViz = new GraphvizChartSetClusteredVisualizer();
        outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-fcs.gv" );
        System.out.println("Writing: " + outfile );
		fcsViz.setDecisionGraph(dg);
        fcsViz.vizualize( outfile );

    }
    
    private String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

}
