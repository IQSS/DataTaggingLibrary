package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetClusteredVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDataStructureVisualizer;
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
    public static void main(String[] args) {
        FlowChartCompiling fcc = new FlowChartCompiling();
        try {
            fcc.go(args);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage() );
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
            
        } catch (BadSetInstructionException ex) {
            TagValueLookupResult badRes = ex.getBadResult();
            
            System.out.println("Bad Set instruction: " + ex.getMessage());
            badRes.accept( new BadSetInstructionPrinter() );
            
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
        
        TagSpaceParser tagsParser = new  TagSpaceParser();
        TagType baseType = tagsParser.parse(readAll(tagsFile)).buildType("DataTags").get();
        
        GraphvizDataStructureVisualizer tagViz = new GraphvizDataStructureVisualizer(baseType);
        Path tagsOutPath = tagsFile.resolveSibling(tagsFile.getFileName().toString() + ".gv");
        System.out.println("Writing " + tagsOutPath );
		tagViz.vizualize( tagsOutPath );
        
        System.out.println("Reading chart: " + chartFile );
        System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")" );
        
        String source = readAll(chartFile);
        
        FlowChartASTParser astParser = new FlowChartASTParser();
        List<InstructionNodeRef> refs = astParser.graphParser().parse(source);
        GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
        Path outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-ast.gv" );
        System.out.println("Writing: " + outfile );
        viz.vizualize( outfile );

        FlowChartSetComplier fcsParser = new FlowChartSetComplier( (CompoundType)baseType );
        FlowChartSet fcs = fcsParser.parse(refs, chartFile.getFileName().toString());
		
//        FlowChart fc = fcs.getFlowChart( fcs.getDefaultChartId() );
//        fcs.addFlowChart(  new EndNodeOptimizer().optimize(fc) );
//        
        GraphvizChartSetClusteredVisualizer fcsViz = new GraphvizChartSetClusteredVisualizer();
        outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-fcs.gv" );
        System.out.println("Writing: " + outfile );
		fcsViz.setChartSet(fcs);
        fcsViz.vizualize( outfile );

    }
    
    private String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

}
