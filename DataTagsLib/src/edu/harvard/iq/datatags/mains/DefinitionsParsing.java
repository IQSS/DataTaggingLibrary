package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParseResult;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDataStructureVisualizer;
import java.io.IOException;
import static java.lang.System.out;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * A test for parsing the initial version of the definitions file.
 * 
 * @author michael
 */
public class DefinitionsParsing {
	
	public static void main(String[] args) throws IOException {
		TagSpaceParser parser = new TagSpaceParser();
		Path base = Paths.get("dtl","0.5");
		Path in = base.resolve("definitions.ts");
		out.println("reading " + in.toAbsolutePath().toString() );
		String source = new String(Files.readAllBytes(in), StandardCharsets.UTF_8);
		
		try {
			TagSpaceParseResult res = parser.parse(source);
            
            out.println("Defined types");
            res.getSlots().forEach( slt -> out.println(slt) );
            
            TagType top = res.buildType("DataTags").get();
			GraphvizDataStructureVisualizer viz = new GraphvizDataStructureVisualizer(top);
            Path outFile = in.resolveSibling(in.getFileName() + ".gv");
			viz.vizualize( outFile);
            out.println("Wrote data to " + outFile.toFile().getAbsolutePath() );
			
		} catch (DataTagsParseException ex) {
			ex.printStackTrace(out);
		}
	}
}
