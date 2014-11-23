package edu.harvard.iq.datatags.mains;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.CompoundType;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.types.TagValueLookupResult;
import edu.harvard.iq.datatags.parser.definitions.DataDefinitionParser;
import edu.harvard.iq.datatags.parser.exceptions.BadSetInstructionException;
import edu.harvard.iq.datatags.parser.exceptions.DataTagsParseException;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartASTParser;
import edu.harvard.iq.datatags.parser.flowcharts.FlowChartSetComplier;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
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

/**
 *
 * @author Naomi
 */
public class ValidCallNodeValidation {
    
//    private final static Node.Visitor<String> NODE_PRINTER = new Node.Visitor<String>(){
//
//        @Override
//        public String visit(AskNode nd) throws DataTagsRuntimeException {
//           return "[AskNode text:" + nd.getText() + "]";
//        }
//
//        @Override
//        public String visit(SetNode nd) throws DataTagsRuntimeException {
//            return "[SetNode sets:" + nd.getTags()
//        }
//
//        @Override
//        public String visit(RejectNode nd) throws DataTagsRuntimeException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String visit(CallNode nd) throws DataTagsRuntimeException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String visit(TodoNode nd) throws DataTagsRuntimeException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//
//        @Override
//        public String visit(EndNode nd) throws DataTagsRuntimeException {
//            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        }
//    };

    public ValidCallNodeValidation() {
    }
    
    public static void main(String[] args) {
        try {
            Path tagsFile = Paths.get(args[0]);
            Path chartFile = Paths.get(args[1]);

            System.out.println("Reading tags: " + tagsFile);
            System.out.println(" (full:  " + tagsFile.toAbsolutePath() + ")");

            DataDefinitionParser tagsParser = new DataDefinitionParser();
            TagType baseType = tagsParser.parseTagDefinitions(readAll(tagsFile), tagsFile.getFileName().toString());

            System.out.println("Reading chart: " + chartFile);
            System.out.println(" (full:  " + chartFile.toAbsolutePath() + ")");

            String source = readAll(chartFile);

            FlowChartASTParser astParser = new FlowChartASTParser();
            List<InstructionNodeRef> refs = astParser.graphParser().parse(source);
            GraphvizGraphNodeRefVizalizer viz = new GraphvizGraphNodeRefVizalizer(refs);
            Path outfile = chartFile.resolveSibling(chartFile.getFileName().toString() + "-ast.gv");
            System.out.println("Writing: " + outfile);
            viz.vizualize(outfile);
            
            System.out.println("AST validations");
            System.out.println("===============");
            
            System.out.println("Validating repeating ids");
            RepeatIdValidator riv = new RepeatIdValidator();
            LinkedList<ValidationMessage> repeatIdMessages = riv.validateRepeatIds(refs);
            if (repeatIdMessages.size() > 0) {
                System.out.println(repeatIdMessages);
            }
            
            FlowChartSetComplier fcsParser = new FlowChartSetComplier((CompoundType) baseType);
            FlowChartSet fcs = fcsParser.parse(refs, chartFile.getFileName().toString());

            System.out.println();
            System.out.println("Semantic validations");
            System.out.println("====================");
            UnreachableNodeValidator unv = new UnreachableNodeValidator();
            System.out.println("Validating unreachable nodes");
            LinkedList<ValidationMessage> unreachableNodeMessages = unv.validateUnreachableNodes(fcs);
            for ( ValidationMessage m : unreachableNodeMessages ) {
                System.out.println(m);
                System.out.println("\t" + m.getEntities());
            }
            
            System.out.println("Validating Call nodes");
            ValidCallNodeValidator fcv = new ValidCallNodeValidator();
            LinkedList<ValidationMessage> callNodeMessages = fcv.validateIdReferences(fcs);
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
