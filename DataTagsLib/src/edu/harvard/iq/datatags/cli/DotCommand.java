package edu.harvard.iq.datatags.cli;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * A base class for commands that use graphviz.
 * @author michael
 */
public abstract class DotCommand implements CliCommand {
    
    private static Path pathToDot;
    
    
    @Override
    public void execute( CliRunner rnr, List<String> args ) throws Exception {
        if ( pathToDot == null ) {
            Optional<Path> dotPath = findDot();
            if ( ! dotPath.isPresent() ) {
                dotPath = promptUserForDotPath( rnr );
            }

            if ( ! dotPath.isPresent() ) {
                rnr.printWarning("Could not find dot. You can install it from www.graphviz.org, or using your platform's package manager.");
                return;
            }
            pathToDot = dotPath.get();
        }
        if ( ! Files.exists(pathToDot)) {
            rnr.printWarning("Dot does not exist in the supplied path `%s`", pathToDot);
            pathToDot = null;
        }
        if ( pathToDot != null ) {
            executeWithDot(pathToDot, rnr, args);
        } else {
            rnr.printWarning("Command cancelled");
        }
    }
    
    protected abstract void executeWithDot( Path dot, CliRunner rnr, List<String> args ) throws Exception;
    
    
     Optional<Path> findDot() {
        String exec = "dot";
        return Stream.of(System.getenv("PATH").split(Pattern.quote(File.pathSeparator)))
            .map(Paths::get)
            .filter(path -> Files.exists(path.resolve(exec))).findFirst().map(p -> p.resolve(exec));
    }

    private Optional<Path> promptUserForDotPath( CliRunner rnr ) throws IOException {
        String dotStr = rnr.readLine("Please supply a path to dot:");
        return ( ! dotStr.isEmpty() ) ? Optional.of( Paths.get(dotStr) ) : Optional.empty();
    }

    protected Path getOuputFilePath(CliRunner rnr, List<String> args, Path basePath, String extension) throws IOException {
        Path outputPath;
        if (args.size() < 2) {
            // try to suggest a file name
            String dgFileName = basePath.getFileName().toString();
            int extensionStart = dgFileName.lastIndexOf(".");
            if (extensionStart > 0) {
                dgFileName = dgFileName.substring(0, extensionStart) + extension;
            }
            Path defaultOutput = basePath.resolveSibling(dgFileName + ".pdf");
            String outputPathFromUser = rnr.readLine("Enter output file name [%s]: ", defaultOutput);
            outputPath = outputPathFromUser.trim().isEmpty() ? defaultOutput : Paths.get(outputPathFromUser.trim());
        } else {
            outputPath = Paths.get(args.get(1));
        }
        return outputPath;
    }
    
}
