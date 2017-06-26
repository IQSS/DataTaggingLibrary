package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.types.CompoundSlot;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.decisiongraph.CompilationUnit;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphCompiler;
import edu.harvard.iq.datatags.parser.tagspace.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.tools.DecisionGraphAstValidator;
import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.RepeatIdValidator;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeAstVisualizer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Runs the following validations on a decision graph:
 * <ul>
 *  <li>Repeated Ids</li>
 *  <li>Unreachable nodes</li>
 *  <li>{@code call} nodes have valid references</li>
 * </ul>
 * @author Naomi
 */
public class DecisionGraphValidations {
    
    public static void main(String[] args) {
        try {
            Path tagsFile = Paths.get(args[0]);
            Path chartFile = Paths.get(args[1]);

            System.out.println("Reading tags: " + tagsFile);
            System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")");

            TagSpaceParser tagsParser = new TagSpaceParser();
            CompoundSlot baseType = tagsParser.parse(readAll(tagsFile)).buildType("DataTags").get();

            System.out.println("Reading chart: " + chartFile);
            System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")");

            String source = readAll(chartFile);

            CompilationUnit cu = new CompilationUnit(source);
            cu.compile(baseType, new EndNode("[SYN-END]"), new ArrayList<>());
            List<? extends AstNode> refs = cu.getParsedFile().getAstNodes();
            
            GraphvizGraphNodeAstVisualizer viz = new GraphvizGraphNodeAstVisualizer(refs);
            Path outfile = chartFile.resolveSibling(chartFile.getFileName().toString() + "-ast.gv");
            System.out.println("Writing: " + outfile);
            viz.vizualize(outfile);
            
            System.out.println("AST validations");
            System.out.println("===============");
            
            System.out.println("Validating repeating ids");
            RepeatIdValidator riv = new RepeatIdValidator();
            List<ValidationMessage> repeatIdMessages = riv.validate(refs);
            if (repeatIdMessages.size() > 0) {
                System.out.println(repeatIdMessages);
            }
            
            DecisionGraph dg = cu.getDecisionGraph();

            System.out.println();
            System.out.println("Semantic validations");
            System.out.println("====================");
            UnreachableNodeValidator unv = new UnreachableNodeValidator();
            System.out.println("Validating unreachable nodes");
            List<ValidationMessage> unreachableNodeMessages = unv.validate(dg);
            unreachableNodeMessages.forEach( m -> {
                System.out.println(m);
                System.out.println("\t" + ((NodeValidationMessage)m).getEntities());
            });
            

            
            
        } catch (IOException ex) {
            System.out.println("Error reading file: " + ex.getMessage());
            System.out.println("Trace:");
            ex.printStackTrace(System.out);

        } catch (BadSetInstructionException ex) {
            TagValueLookupResult badRes = ex.getBadResult();

            System.out.println("Bad Set instruction: " + ex.getMessage());
            badRes.accept(new BadSetInstructionPrinter());

            System.out.println("offending Set node: " + ex.getOffendingNode());

        } catch (DataTagsParseException ex) {
            System.out.println("Semantic Error in data tags program: " + ex.getMessage());
            System.out.println("Trace:");
            ex.printStackTrace(System.out);
        }
    }

    private static String readAll(Path p) throws IOException {
        return new String(Files.readAllBytes(p), StandardCharsets.UTF_8);
    }

}
