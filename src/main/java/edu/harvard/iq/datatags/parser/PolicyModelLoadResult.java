package edu.harvard.iq.datatags.parser;

import java.util.ArrayList;
import java.util.List;
import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstNode;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import java.util.Collection;

/**
 * The result of reading a model. As there are many possible errors and warnings,
 * this class collects them. This class also has a {@link PolicyModel} field, but
 * as it may be only partially loaded, please consult {@link #isSuccessful()}
 * 
 * @author michael
 */
public class PolicyModelLoadResult {
    
    private final List<ValidationMessage> messages = new ArrayList<>();
    
    private PolicyModel model;
    
    private List<? extends AstNode> decisionGraphAst;

    public boolean isSuccessful() {
        return messages.stream().noneMatch( m->m.getLevel()==ValidationMessage.Level.ERROR );
    }
    
    public boolean hasWarnings() {
        return messages.stream().anyMatch( m->m.getLevel()==ValidationMessage.Level.WARNING );
    }
    
    public PolicyModel getModel() {
        return model;
    }

    public void setModel( PolicyModel model ) {
        this.model = model;
    }

    public List<ValidationMessage> getMessages() {
        return messages;
    }
    
    public void addMessage( ValidationMessage msg ){
        messages.add(msg);
    }

    public void addMessages( Collection<ValidationMessage> someMessages ) {
        messages.addAll( someMessages );
    }
    
    public List<? extends AstNode> getDecisionGraphAst() {
        return decisionGraphAst;
    }

    public void setDecisionGraphAst(List<? extends AstNode> decisionGraphAst) {
        this.decisionGraphAst = decisionGraphAst;
    }
    
}
