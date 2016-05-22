package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizTagSpacePathsVizualizer;
import java.awt.Desktop;
import java.io.OutputStreamWriter;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author michael
 */
public class VisualizeTagSpaceCommand extends DotCommand {

    @Override
    public String command() {
        return "visualize-ts";
    }

    @Override
    public String description() {
        return "Creates a visualization of th tag space, as a tree. Users can provide additional parameter for the output file.\n"
                + "Requires graphviz (www.graphviz.org).";
    }
    
    @Override
    protected void executeWithDot(Path dot, CliRunner rnr, List<String> args) throws Exception {
        ProcessBuilder pb = new ProcessBuilder(dot.toString(), "-Tpdf");
        GraphvizTagSpacePathsVizualizer viz = new GraphvizTagSpacePathsVizualizer(rnr.getDecisionGraph().getTopLevelType());
        
        Path outputPath;
        outputPath = getOuputFilePath(rnr, args, rnr.getTagSpacePath(), "-ts");
        
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
