package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChart;
import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.charts.nodes.AskNode;
import edu.harvard.iq.datatags.model.charts.nodes.CallNode;
import edu.harvard.iq.datatags.model.charts.nodes.EndNode;
import edu.harvard.iq.datatags.model.charts.nodes.Node;
import edu.harvard.iq.datatags.model.charts.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.charts.nodes.RejectNode;
import edu.harvard.iq.datatags.model.charts.nodes.SetNode;
import edu.harvard.iq.datatags.model.charts.nodes.TodoNode;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.Answer;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashSet;
import java.util.Set;

/**
 * Traverse the interview and gather all used tag values
 * (used in set nodes).
 *
 * @author Naomi
 */
public class InterviewTagValues extends VoidVisitor {
    private Set<TagValue> usedTagValues = new HashSet<>();
    
    public Set<TagValue> gatherInterviewTagValues(FlowChartSet fcs) {
        for (FlowChart chart : fcs.charts()) {
            for (Node node : chart.nodes()) {
                node.accept(this);
            }
        }
        return usedTagValues;
    }

    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        CompoundValue compound = nd.getTags();
        for (TagType t : compound.getTypesWithNonNullValues()) {
            usedTagValues.add(compound.get(t)); // each tagvalue in this setnode
        }
    }

    @Override
    public void visitImpl(RejectNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(CallNode nd) throws DataTagsRuntimeException {
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
    
}
