package edu.harvard.iq.datatags.parser.decisiongraph;

import java.io.IOException;
import java.nio.file.Path;

/**
 * Gets the string contents for a {@link DecisionGraphCompiler} to read. 
 * Normally this would be from the file system, this could also be from memory
 * (e.g. during unit tests) or from a database.
 * 
 * @author mor_vilozni
 */
public interface ContentReader {
    String getContent(Path path) throws IOException;
}
