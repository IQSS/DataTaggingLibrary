package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAnswerSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstAskNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstCallNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderOptionSubNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstConsiderNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstContinueNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstEndNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode.NullVisitor;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstPartNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstRejectNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSectionNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstTodoNode;
import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import static java.util.stream.Collectors.toList;

/**
 * Checks that every id in the questionnaire is unique. Returns an ERROR with
 * each repeated node id.
 *
 * @author Naomi
 * @author Michael
 */
public class DuplicateIdValidator extends NullVisitor implements DecisionGraphAstValidator {

    private final Map<String, List<AstNode>> nodesById = new HashMap<>();

    @Override
    public List<ValidationMessage> validate(List<? extends AstNode> refs) {
        nodesById.clear();
        
        refs.stream().forEach(ref -> ref.accept(this));
        
        return nodesById.entrySet().stream().filter( ent -> ent.getValue().size()>1 )
                 .map( ent -> new ValidationMessage(Level.ERROR, 
                                        String.format("Duplicate node id: '%s' (nodes: %s)", ent.getKey(), ent.getValue())) ) 
                .collect( toList() );
                
    }

    @Override
    public void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException {
        collect(nd);
        for (AstConsiderOptionSubNode ansRef : nd.getOptions()) {
            for (AstNode node : ansRef.getSubGraph()) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visitImpl(AstAskNode nd) throws DataTagsRuntimeException {
        collect(nd);
        for (AstAnswerSubNode ansRef : nd.getAnswers()) {
            for (AstNode node : ansRef.getSubGraph()) {
                node.accept(this);
            }
        }
    }

    @Override
    public void visitImpl(AstSetNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }

    @Override
    public void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }

    @Override
    public void visitImpl(AstCallNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }

    @Override
    public void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }

    @Override
    public void visitImpl(AstEndNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }
    
    @Override
    public void visitImpl(AstSectionNode nd) throws DataTagsRuntimeException {
        collect(nd);
        for (AstNode an : nd.getAstNodes()) {
            an.accept(this);
        }
    }
    
    @Override
    public void visitImpl(AstPartNode nd) throws DataTagsRuntimeException {
        collect(nd);
        for (AstNode an : nd.getAstNodes()) {
            an.accept(this);
        }
        
    }
    
    @Override
    public void visitImpl(AstContinueNode nd) throws DataTagsRuntimeException {
        collect(nd);
    }
    
    private void collect( AstNode nd ) {
        nodesById.computeIfAbsent(nd.getId(), str -> new ArrayList<>(1)).add(nd);
    }


}
