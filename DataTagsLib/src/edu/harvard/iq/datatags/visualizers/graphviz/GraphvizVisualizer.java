package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Base class for GraphViz visualizers. Contains mainly utility methods.
 * @author michael
 */
public abstract class GraphvizVisualizer {
	protected final Pattern whitespace = Pattern.compile("\\s|-");
    
    private final Map<Character, String> idEncodeMap = new HashMap<>();
    
    public GraphvizVisualizer(){
        String idChars = ".,/~?!()@#$%^&*_+-[] ";
        
        idChars.chars().forEach( intChar -> {
            Character src = (char)intChar;
            String dest = "_" + String.format("%02d", idChars.indexOf(intChar));
            idEncodeMap.put(src, dest);
        });
        
    }
    
	/**
	 * Convenience method to write to a file.
	 * @param outputFile
	 * @throws IOException
	 */
	public void vizualize(Path outputFile) throws IOException {
		try (final BufferedWriter out = Files.newBufferedWriter(outputFile, StandardCharsets.UTF_8)) {
			visualize(out);
		}
	}

	/**
	 * Writes the representation to the output stream
	 * @param out
	 * @throws IOException
	 */
	public void visualize(Writer out) throws IOException {
		PrintWriter bOut = (out instanceof PrintWriter) ? (PrintWriter) out : new PrintWriter(out);
		printHeader(bOut);
		printBody(bOut);
		printFooter(bOut);
		bOut.flush();
	}
	
	protected abstract void printBody( PrintWriter out ) throws IOException;
	
	void printHeader(PrintWriter out) throws IOException {
		out.println("digraph decisionGraph {");
		out.println("edge [fontname=\"Helvetica\" fontsize=\"10\"]");
		out.println("node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
		out.println("rankdir=LR");
	}

	void printFooter(PrintWriter out) throws IOException {
		out.println("}");
	}

	protected String humanTitle(DecisionGraph ent) {
		return ent.getId();
	}

	String sanitizeTitle(String s) {
		return s.replaceAll("\"", "\\\"");
	}
	
    /**
     * Makes sure the id contains only identifier characters, and is still unique.
     * Needed since .dg allows some characters in node ids, that graphviz does not.
     * @param s the .dg language id.
     * @return a graphviz-acceptable id.
     */
	String sanitizeId(String s) {
		StringBuilder sb = new StringBuilder();
        for ( int i=0; i<s.length(); i++ ) {
            sb.append( idEncodeMap.getOrDefault( s.charAt(i), s.substring(i,i+1)) );
        }
        return sb.toString();
	}

    public String wrap( String source ) {
        return wrapAt( source, 40 );
    }
    
    public String wrapAt( String source, int width ) {
        if ( source==null ) return "";
        if ( source.length() <= width ) return source;
        
        StringBuilder out = new StringBuilder();
        char[] input = source.toCharArray();
        
        int lastIdx = 0;
        for ( int i=0; i<input.length; i++ ) {
            if ( input[i] == '\n' ) {
                out.append(input, lastIdx, i-lastIdx);
                lastIdx = i;
            } else {
                if ( i - lastIdx == width ) {
                    int cutPoint = i;
                    while ( cutPoint > lastIdx ) {
                        if ( input[cutPoint]==' ' ) break;
                        cutPoint--;
                    }
                    if ( cutPoint == lastIdx ) {
                        out.append( input, lastIdx, i-lastIdx );
                        out.append("\n");
                        lastIdx = i;
                    } else {
                        out.append( input, lastIdx, cutPoint-lastIdx );
                        out.append("\n");
                        i = cutPoint+1;
                        lastIdx = cutPoint+1;
                    }
                }
            }
        }
        out.append( input, lastIdx, input.length-lastIdx);

        return out.toString();
    }
    
}
