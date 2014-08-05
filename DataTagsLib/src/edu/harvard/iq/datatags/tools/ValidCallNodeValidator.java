package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.cli.BadSetInstructionPrinter;
import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.Node.NullVisitor;
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
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizChartSetVisualizer;
import edu.harvard.iq.datatags.visualizers.graphviz.GraphvizDataStructureVisualizer;
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
public class ValidCallNodeValidator extends NullVisitor {
    
    private final LinkedList<ValidationMessage> validationMessages = new LinkedList<>();
    private FlowChart chart;
    
    
    // Check that each id referenced in a CallNode exists
    public void validateIdReferences(FlowChartSet fcs) {
        Iterable<FlowChart> chartGroup = fcs.charts();
        for (FlowChart c : chartGroup) {
            chart = c;
            Iterable<Node> chartNodes = chart.nodes();
            for (Node allnodes : chartNodes) {
                allnodes.accept(this);
            }
        }
    }
    
    
    @Override
    public void visitImpl (CallNode cn) throws DataTagsRuntimeException {
        Node exists = chart.getNode(cn.getCalleeNodeId());
        if (exists == null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, cn, "Call node calls nonexistent node."));
        }
    }
    
    @Override
    public void visitImpl (AskNode cn) throws DataTagsRuntimeException {
        // do nothing
    }
    
    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(TodoNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(EndNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    
    public LinkedList<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
   

}