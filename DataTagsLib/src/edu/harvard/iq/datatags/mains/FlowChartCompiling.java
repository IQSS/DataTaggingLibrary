package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.SetLookupResult;
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
    
    public static void main(String[] args) {
        FlowChartCompiling fcc = new FlowChartCompiling();
        try {
            fcc.go(args);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage() );
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
            
        } catch (BadSetInstructionException ex) {
            SetLookupResult badRes = ex.getBadResult();
            System.out.println("Bad Set instruction: " + badRes.status);
            switch ( badRes.status ) {
                case Ambiguous:
                    System.out.println(" possible values: " + badRes.values() );
                    break;
                case SlotNotFound:
                    System.out.println("Can't find slot '" + badRes.textValue + "'");
                    break;
                case ValueNotFound:
                    System.out.println("Can't find value " + badRes.textValue + " in type " + badRes.type);
                    break;
                case Success:
                    System.out.println("Should not have gotten here");
                    break;
            }
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
        
        DataDefinitionParser tagsParser = new  DataDefinitionParser();
        TagType baseType = tagsParser.parseTagDefinitions(readAll(tagsFile), tagsFile.getFileName().toString());
        
        System.out.println("Reading chart: " + chartFile );
        System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")" );
        
        String source = readAll(chartFile);
        
        FlowChartSetComplier fcsParser = new FlowChartSetComplier( baseType );
        FlowChartASTParser astParser = new FlowChartASTParser();
        
        List<InstructionNodeRef> refs = astParser.graphParser().parse(source);
        GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
        Path outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-ast.gv" );
        System.out.println("Writing: " + outfile );
        viz.vizualize( outfile );

        FlowChartSet fcs = fcsParser.parse(refs, chartFile.getFileName().toString());
		
        GraphvizChartSetVisualizer fcsViz = new GraphvizChartSetVisualizer();
        outfile = chartFile.resolveSibling( chartFile.getFileName().toString() + "-fcs.gv" );
        System.out.println("Writing: " + outfile );
		fcsViz.setChartSet(fcs);
        fcsViz.vizualize( outfile );

    }
    
    private String readAll( Path p ) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }
}
