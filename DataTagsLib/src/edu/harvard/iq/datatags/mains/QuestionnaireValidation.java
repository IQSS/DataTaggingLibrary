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
import edu.harvard.iq.datatags.tools.DuplicateNodeAnswerValidator;
import edu.harvard.iq.datatags.tools.NodeValidationMessage;
import edu.harvard.iq.datatags.tools.RepeatIdValidator;
import edu.harvard.iq.datatags.tools.UnreachableNodeValidator;
import edu.harvard.iq.datatags.tools.UnusedTagsValidator;
import edu.harvard.iq.datatags.tools.ValidCallNodeValidator;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Naomi
 */
public class QuestionnaireValidation {
    
    public static void main(String[] args) {
        try {
            Path tagsFile = Paths.get(args[0]);
            Path chartFile = Paths.get(args[1]);
            
            DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
            Date dateobj = new Date();
            System.out.println(df.format(dateobj));

            System.out.println("Reading tags: " + tagsFile);
            System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")");

            TagSpaceParser tagsParser = new TagSpaceParser();
            TagType baseType = tagsParser.parse(readAll(tagsFile)).buildType("DataTags").get();

            System.out.println("Reading chart: " + chartFile);
            System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")");

            String source = readAll(chartFile);

            FlowChartASTParser astParser = new FlowChartASTParser();
            List<AstNode> refs = astParser.graphParser().parse(source);
            DecisionGraphParser fcsParser = new DecisionGraphParser((CompoundType) baseType);
            FlowChartSet fcs = fcsParser.parse(refs, chartFile.getFileName().toString());
            
            
            
            UnreachableNodeValidator unv = new UnreachableNodeValidator();
            List<NodeValidationMessage> unreachableNodeMessages = unv.validateUnreachableNodes(fcs);
            if (!unreachableNodeMessages.isEmpty()) {
                System.out.println("*****************\nUNREACHABLE NODES:");
                for (NodeValidationMessage m : unreachableNodeMessages ) {
                    System.out.println(m);
                    System.out.println("\t" + m.getEntities());
                }
            }
            
            ValidCallNodeValidator fcv = new ValidCallNodeValidator();
            List<NodeValidationMessage> callNodeMessages = fcv.validateIdReferences(fcs);
            if (!callNodeMessages.isEmpty()) {
                System.out.println("*****************\nNONEXISTENT NODES:");
            for (NodeValidationMessage m : callNodeMessages ) {
                    System.out.println(m);
                }
            }
            
            RepeatIdValidator riv = new RepeatIdValidator();
            Set<ValidationMessage> repeatIdMessages = riv.validateRepeatIds(refs);
            if (!repeatIdMessages.isEmpty()) {
                System.out.println("*****************\nREPEATED IDS:");
                for (ValidationMessage m : repeatIdMessages) {
                    System.out.println(m);
                }
            }
            
            DuplicateNodeAnswerValidator dupNode = new DuplicateNodeAnswerValidator();
            List<ValidationMessage> duplicateNodeMessages = dupNode.validateDuplicateAnswers(refs);
            if (!duplicateNodeMessages.isEmpty()) {
                System.out.println("*****************\nDUPLICATE ANSWERS:");
                for ( ValidationMessage m : duplicateNodeMessages) {
                    System.out.println(m);
                }
            }
            
            UnusedTagsValidator utv = new UnusedTagsValidator();
            List<ValidationMessage> unusedTagsMessages = utv.validateUnusedTags(fcs);
            if (!unusedTagsMessages.isEmpty()) {
                System.out.println("*****************UNUSED TAGS:");
                for (ValidationMessage m : unusedTagsMessages) {
                    System.out.println(m);
                }
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
