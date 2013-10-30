/*
 *  (C) Michael Bar-Sinai
 */

package edu.harvard.iq.datatags.visualizers.graphviz;

import edu.harvard.iq.datatags.runtime.RuntimeEntity;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Pattern;

/**
 *
 * @author michael
 */
public abstract class GraphvizVisualizer {
	protected final Pattern whitespace = Pattern.compile("\\s|-");

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
		BufferedWriter bOut = (out instanceof BufferedWriter) ? (BufferedWriter) out : new BufferedWriter(out);
		printHeader(bOut);
		printBody(bOut);
		printFooter(bOut);
		bOut.flush();
	}
	
	protected abstract void printBody( BufferedWriter out ) throws IOException;
	
	void printHeader(BufferedWriter out) throws IOException {
		out.write("digraph ChartSet {");
		out.newLine();
		out.write("edge [fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write("node [fillcolor=\"lightgray\" style=\"filled\" fontname=\"Helvetica\" fontsize=\"10\"]");
		out.newLine();
		out.write("rankdir=LR");
		out.newLine();
	}

	void printFooter(BufferedWriter out) throws IOException {
		out.write("}");
		out.newLine();
	}

	protected String humanTitle(RuntimeEntity ent) {
		return (ent.getTitle() != null) ? String.format("%s: %s", ent.getId(), ent.getTitle()) : ent.getId();
	}

	String sanitize(String s) {
		return whitespace.matcher(s.trim()).replaceAll("_");
	}
	
}
