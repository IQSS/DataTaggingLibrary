package edu.harvard.iq.datatags.parser.decisiongraph;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * An implementation of {@link ContentReader} that reads files from the 
 * default file system.
 * 
 * @author mor_vilozni
 */
public class FileSystemContentReader implements ContentReader {

    /**
     *
     * @param path
     * @return
     * @throws java.io.IOException
     */
    @Override
    public String getContent(Path path) throws IOException {
      return (new String(Files.readAllBytes(path), StandardCharsets.UTF_8));
    }
    
}
