package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import java.util.List;

/**
 *
 * @author mor
 */
public class AstPartNode extends AstNode{

    private List<? extends AstNode> astNodes;

    public AstPartNode(String id, List<? extends AstNode> astNodes) {
        super(id);
        this.astNodes = astNodes;
    }
    
    public AstPartNode(String id) {
        super(id);
    }
    
    public List<? extends AstNode> getAstNodes(){
        return this.astNodes;
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }
    
}
