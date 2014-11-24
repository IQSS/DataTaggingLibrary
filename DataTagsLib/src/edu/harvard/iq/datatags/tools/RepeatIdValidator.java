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
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks that every id in the questionnaire is unique.
 * Returns an ERROR with each repeated node id.
 * 
 * @author Naomi
 */
public class RepeatIdValidator extends NullVisitor {
    
    private HashSet<String> seenIds = new HashSet<>();
    private LinkedList<ValidationMessage> validationMessages = new LinkedList<>();
    
    public List<ValidationMessage> validateRepeatIds(List<InstructionNodeRef> refs) {
        for (InstructionNodeRef ref : refs) {
            ref.accept(this);
        }
        return validationMessages;
    }

    @Override
    public void visitImpl(AskNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
                validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
        for (AnswerNodeRef ansRef : nd.getAnswers()) {
            for (InstructionNodeRef node : ansRef.getImplementation()) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visitImpl(SetNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(RejectNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }    
    }

    @Override
    public void visitImpl(CallNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(TodoNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(EndNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

   
    
}