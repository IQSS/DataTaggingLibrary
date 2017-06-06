package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.io.PolicyModelDataParser;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.PolicyModelData;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.PolicyModelLoadResult;
import edu.harvard.iq.datatags.parser.PolicyModelLoader;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDecisionGraphClusteredVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDecisionGraphF11Visualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeAstVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizTagSpacePathsVizualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizTagSpaceVisualizer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Compiles and visualizes a tag space and a decision graph.
 *
 * @author michael
 */
public class DecisionGraphCompiling {

    public static void main(String[] args) throws Exception {
        DecisionGraphCompiling fcc = new DecisionGraphCompiling();
        try {
            fcc.go(args);
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
            System.out.println("Trace:");
            ex.printStackTrace(System.out);

        } catch (BadSetInstructionException ex) {
            TagValueLookupResult badRes = ex.getBadResult();

            if (badRes != null) {
                System.out.println("Bad Set instruction: " + ex.getMessage());
                badRes.accept(new BadSetInstructionPrinter());
            }
            System.out.println("Error in [set] node: " + ex.getMessage());
            System.out.println("offending Set node: " + ex.getOffendingNode());

        } catch (DataTagsParseException ex) {
            System.out.println("Semantic Error in data tags program: " + ex.getMessage());
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
        }
    }

    public void go(String[] args) throws Exception {
        Path modelFile = Paths.get(args[0]);
        
        System.out.println("Loading " + modelFile);
        
        PolicyModelDataParser pmdParser = new PolicyModelDataParser();
        PolicyModelData readResult = pmdParser.read(modelFile);
        PolicyModelLoadResult loadRes = PolicyModelLoader.verboseLoader().load(readResult);
        
        modelFile = readResult.getMetadataFile();

        if (loadRes.isSuccessful()) {
            System.out.printf("Model '%s' loaded\n", loadRes.getModel().getMetadata().getTitle());
        } else {
            System.out.println("Failed to load model: ");
        }

        if (!loadRes.getMessages().isEmpty()) {
            System.out.println("Load Messages");
            loadRes.getMessages().forEach(m -> System.out.println(m.getLevel() + "   " + m.getMessage()));
        }
        
        if ( !loadRes.isSuccessful() ) {
            System.exit(1);
        }
        
        PolicyModel model = loadRes.getModel();
        GraphvizTagSpaceVisualizer tagViz = new GraphvizTagSpaceVisualizer(model.getSpaceRoot());
        Path tagsOutPath = modelFile.resolveSibling(modelFile.getFileName().toString() + ".ps-plain.gv");
        System.out.println("Writing " + tagsOutPath);
        tagViz.vizualize(tagsOutPath);
        
        GraphvizTagSpacePathsVizualizer tagPViz = new GraphvizTagSpacePathsVizualizer(model.getSpaceRoot());
        tagsOutPath = modelFile.resolveSibling(modelFile.getFileName().toString() + ".ps-paths.gv");
        System.out.println("Writing " + tagsOutPath);
        tagPViz.vizualize(tagsOutPath);

        // AST Visualizer
        GraphvizGraphNodeAstVisualizer viz = new GraphvizGraphNodeAstVisualizer(loadRes.getDecisionGraphAst());
        Path outfile = modelFile.resolveSibling(modelFile.getFileName().toString() + "-ast.gv");
        System.out.println("Writing: " + outfile);
        viz.vizualize(outfile);


        DecisionGraph dg = model.getDecisionGraph();

        GraphvizDecisionGraphClusteredVisualizer fcsViz = new GraphvizDecisionGraphClusteredVisualizer();
        outfile = modelFile.resolveSibling(modelFile.getFileName().toString() + ".dg.gv");
        System.out.println("Writing: " + outfile);
        fcsViz.setDecisionGraph(dg);
        fcsViz.vizualize(outfile);

        GraphvizDecisionGraphF11Visualizer f11Viz = new GraphvizDecisionGraphF11Visualizer();
        outfile = modelFile.resolveSibling(modelFile.getFileName().toString() + ".dg-f11.gv");
        System.out.println("Writing: " + outfile);
        f11Viz.setDecisionGraph(dg);
        f11Viz.vizualize(outfile);

    }

}
