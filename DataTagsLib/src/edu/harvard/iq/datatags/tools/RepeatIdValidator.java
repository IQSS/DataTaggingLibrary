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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Checks that every id in the questionnaire is unique.
 * Returns an ERROR with each repeated node id.
 * 
 * @author Naomi
 */
public class RepeatIdValidator extends NullVisitor {
    
    private final Set<String> seenIds = new HashSet<>();
    private final Map<String, ValidationMessage> validationMessages = new TreeMap<>();
    
    public Set<ValidationMessage> validateRepeatIds(List<InstructionNodeRef> refs) {
        refs.stream().forEach( ref -> ref.accept(this) );
        return new HashSet<>(validationMessages.values());
    }

    @Override
    public void visitImpl(AskNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
                validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
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
            validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(RejectNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }    
    }

    @Override
    public void visitImpl(CallNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(TodoNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(EndNodeRef nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put( nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

   
    
}