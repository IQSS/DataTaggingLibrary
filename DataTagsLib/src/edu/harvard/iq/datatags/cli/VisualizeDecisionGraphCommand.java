package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.visualizers.graphviz.AbstractGraphvizDecisionGraphVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetClusteredVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetF11Visualizer;
import java.awt.Desktop;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import static java.util.stream.Collectors.toSet;

/**
 * Visualizes the decision graph, using graphviz.
 * @author michael
 */
public class VisualizeDecisionGraphCommand extends DotCommand {

    @Override
    public String command() {
        return "visualize-dg";
    }

    @Override
    public String description() {
        return "Creates a visualization of the decision graph. Requires graphviz (www.graphviz.org).\n"
                + "Can be invoked with a parameter for output file name. The filename extension is used to determine"
                + "the output format (options are .pdf .png .gv .jpg .svg).\n"
                + "Invoke with `-style=f11` for alternative graph styling.\n"
                + "When using f11 style, end nodes are typically not drawn; use -show-ends to draw them as well.";
    }

    @Override
    public void executeWithDot(Path pathToDot, CliRunner rnr, List<String> args) throws Exception {
        
        
        Path outputPath;
        outputPath = getOuputFilePath(rnr, args, rnr.getModel().getMetadata().getDecisionGraphPath(), "-dg");
        
        String[] fileNameComponents = outputPath.getFileName().toString().split("\\.");
        String fileExtension = (fileNameComponents.length>1)?fileNameComponents[fileNameComponents.length-1]:"pdf";
        
        ProcessBuilder pb = new ProcessBuilder(pathToDot.toString(), "-T" + fileExtension);
        
        Set<String> argSet = args.stream().map(s->s.toLowerCase()).collect(toSet());
        AbstractGraphvizDecisionGraphVisualizer viz = argSet.contains("-style=f11") ? new GraphvizChartSetF11Visualizer(argSet.contains("-show-ends")) : new GraphvizChartSetClusteredVisualizer();
        viz.setDecisionGraph(rnr.getModel().getDecisionGraph());
        
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
