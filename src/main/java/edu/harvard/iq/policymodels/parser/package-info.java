/**
 * 
 * Parsers of tag spaces and decision graphs, and their related classes live here.
 * This library uses a multi-pass parsing process, so the sub packages include many classes.
 * Typical structure is:
 * <ol>
 *  <li>{@code XXXTerminalParser} - tokenizer.</li>
 *  <li>{@code XXXRuleParser} - generative grammar AST builder.<li>
 *  <li>{@code XXXParseResult} - Result of parsing a file. Can be used to produce the actual runtime structures (e.g. {@link edu.harvard.iq.datatags.model.graphs.nodes.Node}s),
 *                               or get a finer-grained view of the parsed file.</li>
 *  <li>{@code XXXParser} - Typical client entry point for the package. Produces a {@code XXXParseResult}</li>
 * </ol>
 * 
 * 
 * @author michael
 */
package edu.harvard.iq.policymodels.parser;
