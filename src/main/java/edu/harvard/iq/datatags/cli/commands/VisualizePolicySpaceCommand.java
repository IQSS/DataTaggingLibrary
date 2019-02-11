package edu.harvard.iq.datatags.cli.commands;

import edu.harvard.iq.datatags.cli.CliRunner;
import edu.harvard.iq.datatags.cli.ProcessOutputDumper;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizTagSpacePathsVizualizer;
import java.awt.Desktop;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 *
 * @author michael
 */
public class VisualizePolicySpaceCommand extends DotCommand {

    @Override
    public String command() {
        return "visualize-ps";
    }

    @Override
    public String description() {
        return "Creates a visualization of the policy space, as a tree. Users can provide additional parameter for the output file.\n"
                + "Requires graphviz (www.graphviz.org).";
    }
    
    @Override
    protected void executeWithDot(Path pathToDot, CliRunner rnr, List<String> args) throws Exception {
        GraphvizTagSpacePathsVizualizer viz = new GraphvizTagSpacePathsVizualizer(rnr.getModel().getSpaceRoot());
        
        Path outputPath;
        outputPath = getOuputFilePath(rnr, args, rnr.getModel().getMetadata().getPolicySpacePath(), "-ps");
        
        String[] fileNameComponents = outputPath.getFileName().toString().split("\\.");
        String fileExtension = (fileNameComponents.length>1)?fileNameComponents[fileNameComponents.length-1]:"pdf";
        
         if ( fileExtension.equals("gv") || fileExtension.equals("dot") ) {
            try (Writer outputToFile = Files.newBufferedWriter(outputPath) ) {
                viz.visualize(outputToFile);
            }
            
        } else {
            ProcessBuilder pb = new ProcessBuilder(pathToDot.toString(), "-T" + fileExtension);
        
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

    
}
