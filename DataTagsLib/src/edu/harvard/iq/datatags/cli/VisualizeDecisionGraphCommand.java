package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.tools.EndNodeOptimizer;
import edu.harvard.iq.datatags.tools.TagSpaceOptimizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetClusteredVisualizer;
import java.awt.Desktop;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;

/**
 * Visualizes the decision graph, using graphviz.
 * @author michael
 */
public class VisualizeDecisionGraphCommand extends DotCommand {

    Path pathToDot;
    
    @Override
    public String command() {
        return "visualize-dg";
    }

    @Override
    public String description() {
        return "Creates a visualization of the decision graph. Requires graphviz (www.graphviz.org).\n"
                + "Can be invoked with a parameter for output file name.";
    }

    @Override
    public void executeWithDot(Path pathToDot, CliRunner rnr, List<String> args) throws Exception {

        DecisionGraph fcs;
        ProcessBuilder pb = new ProcessBuilder(pathToDot.toString(), "-Tpdf");
        GraphvizChartSetClusteredVisualizer viz = new GraphvizChartSetClusteredVisualizer();

        // Optimize graph
        fcs = rnr.getDecisionGraph();
        TagSpaceOptimizer tagSpaceOptimizer = new TagSpaceOptimizer();
        fcs = tagSpaceOptimizer.optimize(fcs);
        viz.setDecisionGraph(fcs);
        
        Path outputPath;
        outputPath = getOuputFilePath(rnr, args, rnr.getDecisionGraphPath(), "");
        
        Process gv = pb.start();
        try (OutputStreamWriter outputToGraphviz = new OutputStreamWriter(gv.getOutputStream())) {
            viz.visualize(outputToGraphviz);
        }
        
        ProcessOutputDumper dump = new ProcessOutputDumper( gv.getInputStream(), outputPath);
        dump.start();
        
        int statusCode = gv.waitFor();
        if ( statusCode != 0 ) {
            rnr.printWarning("Graphviz terminated with an error (exit code: %d)", statusCode);
        } else {
            dump.await();
            rnr.println("File created at: %s", outputPath.toRealPath());
            if ( Desktop.isDesktopSupported() ) {
                Desktop.getDesktop().open(outputPath.toFile());
            }
        }
        
    }

    
}
