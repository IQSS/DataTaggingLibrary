package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.parser.exceptions.SemanticsErrorException;
import edu.harvard.iq.datatags.parser.exceptions.SyntaxErrorException;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizTagSpacePathsVizualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizVisualizer;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Run visualizers (mainly for testing them).
 * @author michael
 */
public class Visualizers {
    
    
    public static void main(String[] args) throws IOException, SyntaxErrorException, SemanticsErrorException {
        Path tagsFile = Paths.get(args[0]);
        
        System.out.println("Reading tags: " + tagsFile );
        System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")" );
        
        TagSpaceParser tagsParser = new TagSpaceParser();
        CompoundSlot baseType = tagsParser.parse(tagsFile).buildType("DataTags").get();
        
        GraphvizVisualizer tagViz = new GraphvizTagSpacePathsVizualizer(baseType);
        Path tagsOutPath = tagsFile.resolveSibling(tagsFile.getFileName().toString() + ".gv");
        System.out.println("Writing " + tagsOutPath );
		tagViz.vizualize( tagsOutPath );
    }
    
}
