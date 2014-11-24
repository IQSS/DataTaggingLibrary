package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.flowcharts.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef.NullVisitor;
import edu.harvard.iq.datatags.parser.flowcharts.references.RejectNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.TodoNodeRef;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks for nodes with duplicate answers.
 * Returns list of these nodes.
 * @author Naomi
 */
public class DuplicateNodeAnswerValidator extends NullVisitor{
    
    private LinkedList<InstructionNodeRef> duplicateAnswers;
    
    public List<InstructionNodeRef> validateDuplicateAnswers(List<InstructionNodeRef> allRefs) {
        duplicateAnswers = new LinkedList<>();
        for (InstructionNodeRef ref: allRefs) {
            ref.accept(this);
        }
        return duplicateAnswers;
    }

    @Override
    public void visitImpl(AskNodeRef nd) throws DataTagsRuntimeException {
        LinkedList<AnswerNodeRef> noduplicates = new LinkedList<>();
        for (AnswerNodeRef ansRef : nd.getAnswers()) {
            for (InstructionNodeRef implementation: ansRef.getImplementation()) {
                implementation.accept(this); // descend through the questionnaire structure
            }
            for (AnswerNodeRef ans : noduplicates) {
                // compare answer text, since we don't want two no answers that have different implementations
                if (ansRef.getAnswerText().equals(ans.getAnswerText())) {
                    duplicateAnswers.add(nd);
                }
            }
            noduplicates.add(ansRef);
        }
    }

    @Override
    public void visitImpl(SetNodeRef nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(RejectNodeRef nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(CallNodeRef nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(TodoNodeRef nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(EndNodeRef nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }


    
}
