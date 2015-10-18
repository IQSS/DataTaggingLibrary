package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.graphs.FlowChartSet;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.definitions.TagSpaceParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.decisiongraph.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.decisiongraph.DecisionGraphParser;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.RepeatIdValidator;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.ValidCallNodeValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizGraphNodeRefVizalizer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Naomi
 */
public class ValidCallNodeValidation {
    
    public ValidCallNodeValidation() {
    }
    
    public static void main(String[] args) {
        try {
            Path tagsFile = Paths.get(args[0]);
            Path chartFile = Paths.get(args[1]);

            System.out.println("Reading tags: " + tagsFile);
            System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")");

            TagSpaceParser tagsParser = new TagSpaceParser();
            TagType baseType = tagsParser.parse(readAll(tagsFile)).buildType("DataTags").get();

            System.out.println("Reading chart: " + chartFile);
            System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")");

            String source = readAll(chartFile);

            FlowChartASTParser astParser = new FlowChartASTParser();
            List<AstNode> refs = astParser.graphParser().parse(source);
            GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
            Path outfile = chartFile.resolveSibling(chartFile.getFileName().toString() + "-ast.gv");
            System.out.println("Writing: " + outfile);
            viz.vizualize(outfile);
            
            System.out.println("AST validations");
            System.out.println("===============");
            
            System.out.println("Validating repeating ids");
            RepeatIdValidator riv = new RepeatIdValidator();
            Set<ValidationMessage> repeatIdMessages = riv.validateRepeatIds(refs);
            if (repeatIdMessages.size() > 0) {
                System.out.println(repeatIdMessages);
            }
            
            DecisionGraphParser fcsParser = new DecisionGraphParser((CompoundType) baseType);
            FlowChartSet fcs = fcsParser.parse(refs, chartFile.getFileName().toString());

            System.out.println();
            System.out.println("Semantic validations");
            System.out.println("====================");
            UnreachableNodeValidator unv = new UnreachableNodeValidator();
            System.out.println("Validating unreachable nodes");
            List<NodeValidationMessage> unreachableNodeMessages = unv.validateUnreachableNodes(fcs);
            unreachableNodeMessages.stream().map((m) -> {
                System.out.println(m);
                return m;
            }).forEach((m) -> {
                System.out.println("\t" + m.getEntities());
            });
            
            System.out.println("Validating Call nodes");
            ValidCallNodeValidator fcv = new ValidCallNodeValidator();
            LinkedList<NodeValidationMessage> callNodeMessages = fcv.validateIdReferences(fcs);
            if (callNodeMessages.size() > 0) {
                System.out.println(callNodeMessages);
                System.exit(-1);
            }

            
            
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
