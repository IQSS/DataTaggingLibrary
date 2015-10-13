package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.references.AnswerNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AskNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.CallNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.EndNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.InstructionNodeRef.NullVisitor;
import edu.harvard.iq.datatags.parser.decisiongraph.references.RejectNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.SetNodeRef;
import edu.harvard.iq.datatags.parser.decisiongraph.references.TodoNodeRef;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks for nodes with duplicate answers.
 * Returns list of these nodes.
 * @author Naomi
 */
public class DuplicateNodeAnswerValidator extends NullVisitor{
    
    private List<ValidationMessage> validationMessages = new LinkedList<>();
    
    public List<ValidationMessage> validateDuplicateAnswers(List<InstructionNodeRef> allRefs) {
        validationMessages = new LinkedList<>();
        allRefs.stream().forEach( ref -> ref.accept(this) );
        return validationMessages;
    }

    @Override
    public void visitImpl(AskNodeRef nd) throws DataTagsRuntimeException {
        List<AnswerNodeRef> noduplicates = new LinkedList<>();
        for (AnswerNodeRef ansRef : nd.getAnswers()) {
            for (InstructionNodeRef implementation: ansRef.getImplementation()) {
                implementation.accept(this); // descend through the questionnaire structure
            }
            for (AnswerNodeRef ans : noduplicates) {
                // compare answer text, since we don't want two no answers that have different implementations
                if (ansRef.getAnswerText().equals(ans.getAnswerText())) {
                    validationMessages.add(new ValidationMessage(Level.WARNING, "Ask node \"" + nd.getId() + "\" has duplicate answers"));
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
