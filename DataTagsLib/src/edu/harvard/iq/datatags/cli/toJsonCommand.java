
package edu.harvard.iq.datatags.cli;

import edu.harvard.iq.datatags.visualizers.html.JsonFactory;
import java.awt.Desktop;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.json.simple.JSONObject;

/**
 * exports the decision graph and tag space into a JSON file
 *
 * @author Yonatan Tzulang
 */
public class toJsonCommand implements CliCommand{

    @Override
    public String command() {
        return "toJson";
    }

    @Override
    public String description() {
        return "Exports data into json file. Users can provide additional parameter for the output file";
    }

    @Override
    public void execute(CliRunner rnr, List<String> args) throws Exception {
        
        Path outputPath = getOuputFilePath(rnr, args, rnr.getTagSpacePath());
        JSONObject output=JsonFactory.toJson(rnr.getDecisionGraph().getTopLevelType(), rnr.getDecisionGraph()); 
        
        try (Writer fileWriter = new FileWriter(outputPath.toString())) {
            fileWriter.write(output.toJSONString()); 
            fileWriter.close();
        }        
         rnr.println("File created at: %s", outputPath.toRealPath());
            if ( Desktop.isDesktopSupported() ) {
                Desktop.getDesktop().open(outputPath.toFile());
            }
    }
    
    private Path getOuputFilePath(CliRunner rnr, List<String> args, Path basePath) throws IOException {
        Path outputPath;
        if (args.size() < 2) {
            // try to suggest a file name
            String dgFileName = basePath.getFileName().toString();
            int extensionStart = dgFileName.lastIndexOf(".");
            if (extensionStart > 0) {
                dgFileName = dgFileName.substring(0, extensionStart);
            }
            Path defaultOutput = basePath.resolveSibling(dgFileName + ".json");
            String outputPathFromUser = rnr.readLine("Enter output file name [%s]: ", defaultOutput);
            outputPath = outputPathFromUser.trim().isEmpty() ? defaultOutput : Paths.get(outputPathFromUser.trim());
        } else {
            outputPath = Paths.get(args.get(1));
        }
        return outputPath;
    }
}
