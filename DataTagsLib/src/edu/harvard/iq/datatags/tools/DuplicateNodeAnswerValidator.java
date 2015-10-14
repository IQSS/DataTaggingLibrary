package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.references.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstNode.NullVisitor;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.references.AstTodoNode;
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
    
    public List<ValidationMessage> validateDuplicateAnswers(List<AstNode> allRefs) {
        validationMessages = new LinkedList<>();
        allRefs.stream().forEach( ref -> ref.accept(this) );
        return validationMessages;
    }

    @Override
    public void visitImpl(AstAskNode nd) throws DataTagsRuntimeException {
        List<AstAnswerSubNode> noduplicates = new LinkedList<>();
        for (AstAnswerSubNode ansRef : nd.getAnswers()) {
            for (AstNode implementation: ansRef.getSubGraph()) {
                implementation.accept(this); // descend through the questionnaire structure
            }
            for (AstAnswerSubNode ans : noduplicates) {
                // compare answer text, since we don't want two no answers that have different implementations
                if (ansRef.getAnswerText().equals(ans.getAnswerText())) {
                    validationMessages.add(new ValidationMessage(Level.WARNING, "Ask node \"" + nd.getId() + "\" has duplicate answers"));
                }
            }
            noduplicates.add(ansRef);
        }
    }

    @Override
    public void visitImpl(AstSetNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(AstCallNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(AstEndNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }


    
}
