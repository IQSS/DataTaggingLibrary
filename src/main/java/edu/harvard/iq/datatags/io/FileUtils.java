package edu.harvard.iq.datatags.io;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import static java.util.stream.Collectors.joining;

/**
 * Utility class for handling files.
 * @author michael
 */
public class FileUtils {
    
    public static String readAll(Path aPath) {
        try {
            return Files.readAllLines(aPath, StandardCharsets.UTF_8).stream().collect(joining("\n"));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }
    
    public static Path ciResolve( Path aPath, String aFileName ) throws IOException {
        String lcFileName = aFileName.toLowerCase();
        return Files.find(aPath, 1, (p,_opts)->p.getFileName().toString().toLowerCase().equals(lcFileName))
                    .findAny().orElse(null);
    }
    
}
