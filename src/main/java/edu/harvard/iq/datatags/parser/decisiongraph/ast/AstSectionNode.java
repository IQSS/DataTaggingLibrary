/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import edu.harvard.iq.datatags.model.graphs.nodes.Node;
import java.util.List;

/**
 *
 * @author mor_vilozni
 */
public class AstSectionNode extends AstNode{

    private AstInfoSubNode info;
    private List<? extends AstNode> astNodes;
    
    public AstSectionNode(String id, AstInfoSubNode info, List<? extends AstNode> astNodes) {
        super(id);
        this.info = info;
        this.astNodes = astNodes;
    }
    
    public AstSectionNode(String id, List<? extends AstNode> astNodes) {
        super(id);
        this.astNodes = astNodes;
    }
    
    public AstInfoSubNode getInfo(){
        return this.info;
    }

    public AstSectionNode(String id) {
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
