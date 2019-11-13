package edu.harvard.iq.policymodels.tools;

import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstConsiderOptionSubNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstContinueNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstNode.NullVisitor;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstPartNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstSectionNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.policymodels.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.policymodels.tools.ValidationMessage.Level;
import java.util.LinkedList;
import java.util.List;

/**
 * Checks for nodes with duplicate answers.
 * Returns list of these nodes.
 * @author Naomi
 */
public class DuplicateNodeAnswerValidator extends NullVisitor  implements DecisionGraphAstValidator{
    
    private List<ValidationMessage> validationMessages = new LinkedList<>();
    
    @Override
    public List<ValidationMessage> validate(List<? extends AstNode> allRefs) {
        validationMessages = new LinkedList<>();
        allRefs.stream().forEach( ref -> ref.accept(this) );
        return validationMessages;
    }
      @Override
    public void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException {
        List<AstConsiderOptionSubNode> noduplicates = new LinkedList<>();
        for (AstConsiderOptionSubNode ansRef : nd.getOptions()) {
            for (AstNode implementation: ansRef.getSubGraph()) {
                implementation.accept(this); // descend through the questionnaire structure
            }
            for (AstConsiderOptionSubNode ans : noduplicates) {
                // compare answer text, since we don't want two no answers that have different implementations
                if ( ansRef.getOptionList() != null && ans.getOptionList() != null ) {
                    if (ansRef.getOptionList().equals(ans.getOptionList())) {
                        validationMessages.add(new ValidationMessage(Level.WARNING, "consider node \"" + nd.getId() + "\" has duplicate answers"));
                    }
                }
            }
            noduplicates.add(ansRef);
        }
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
    
    @Override
    public void visitImpl(AstSectionNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }
    
    @Override
    public void visitImpl(AstPartNode nd) throws DataTagsRuntimeException {
        // do nothing unless node ref is an AskNodeRef
    }

    @Override
    public void visitImpl(AstContinueNode nd) throws DataTagsRuntimeException {
        //
    }
    

    
}
