/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.parser.decisiongraph.ast;

/**
 *
 * @author mor
 */
public class AstContinueNode extends AstNode {

    public AstContinueNode(String id) {
        super(id);
    }
    
    public AstContinueNode() {
        super(null);
    }

    @Override
    public <T> T accept(Visitor<T> v) {
        return v.visit(this);
    }

    @Override
    public boolean equals(Object obj) {
        if ( obj == null ) return false;
        if ( obj == this ) return true;
        if ( !(obj instanceof AstContinueNode) ) {
            return false;
        }
        return super.equals(obj); 
    }

    @Override
    public int hashCode() {
        return super.hashCode(); 
    }

    
    
}
