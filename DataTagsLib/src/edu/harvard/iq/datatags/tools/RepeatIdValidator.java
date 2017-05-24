package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode.NullVisitor;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

/**
 * Checks that every id in the questionnaire is unique. Returns an ERROR with
 * each repeated node id.
 *
 * @author Naomi
 */
public class RepeatIdValidator extends NullVisitor implements DecisionGraphAstValidator {

    private final Set<String> seenIds = new HashSet<>();
    private final Map<String, ValidationMessage> validationMessages = new TreeMap<>();

    @Override
    public List<ValidationMessage> validate(List<? extends AstNode> refs) {
        refs.stream().forEach(ref -> ref.accept(this));
        return new ArrayList(validationMessages.values());
    }

    @Override
    public void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
        for (AstConsiderAnswerSubNode ansRef : nd.getAnswers()) {
            for (AstNode node : ansRef.getSubGraph()) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visitImpl(AstAskNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
        for (AstAnswerSubNode ansRef : nd.getAnswers()) {
            for (AstNode node : ansRef.getSubGraph()) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visitImpl(AstSetNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(AstCallNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

    @Override
    public void visitImpl(AstEndNode nd) throws DataTagsRuntimeException {
        if (seenIds.contains(nd.getId()) && nd.getId() != null) {
            validationMessages.put(nd.getId(), new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + nd.getId() + "\"."));
        } else {
            seenIds.add(nd.getId());
        }
    }

}
