package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.graphs.nodes.AskNode;
import edu.harvard.iq.datatags.model.graphs.nodes.CallNode;
import edu.harvard.iq.datatags.model.graphs.nodes.EndNode;
import edu.harvard.iq.datatags.model.graphs.nodes.Node.VoidVisitor;
import edu.harvard.iq.datatags.model.graphs.nodes.RejectNode;
import edu.harvard.iq.datatags.model.graphs.nodes.SetNode;
import edu.harvard.iq.datatags.model.graphs.nodes.TodoNode;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.HashSet;
import java.util.Set;

/**
 * Traverse the questionnaire and gather all used tag values
 * (used in set nodes).
 *
 * @author Naomi
 */
public class QuestionnaireTagValues extends VoidVisitor {
    private final Set<TagValue> usedTagValues = new HashSet<>();
    
    public Set<TagValue> gatherInterviewTagValues( DecisionGraph dg ) {
        dg.nodes().forEach( a -> a.accept(this));
        return usedTagValues;
    }

    @Override
    public void visitImpl(AskNode nd) throws DataTagsRuntimeException {
        // do nothing
    }

    @Override
    public void visitImpl(SetNode nd) throws DataTagsRuntimeException {
        CompoundValue compound = nd.getTags();
        compound.getTypesWithNonNullValues().forEach((t) -> {
            usedTagValues.add(compound.get(t)); // each tagvalue in this setnode
        });
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
