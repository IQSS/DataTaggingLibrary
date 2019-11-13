/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.policymodels.parser.decisiongraph.ast;

import edu.harvard.iq.policymodels.parser.decisiongraph.AstNodeIdProvider;
import edu.harvard.iq.policymodels.runtime.exceptions.DataTagsRuntimeException;
import java.util.List;

/**
 *
 * @author mor_vilozni
 */
public class NodeIdAdder extends AstNode.NullVisitor {

    private final AstNodeIdProvider nodeIdProvider = new AstNodeIdProvider();

    public void addIds( List<? extends AstNode> nodes ) {
         nodes.forEach(n -> n.accept(this));
    }
    
    @Override
    public void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
        nd.getOptions().forEach(ans -> addIds(ans.getSubGraph()));
        if (nd.getElseNode() != null) {
            addIds(nd.getElseNode());
        }
    }

    @Override
    public void visitImpl(AstAskNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
        nd.getAnswers().forEach(ans -> addIds(ans.getSubGraph()));
    }

    @Override
    public void visitImpl(AstSetNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }

    @Override
    public void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }

    @Override
    public void visitImpl(AstCallNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }

    @Override
    public void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }

    @Override
    public void visitImpl(AstEndNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }

    @Override
    public void visitImpl(AstSectionNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
        addIds(nd.getAstNodes());
    }

    @Override
    public void visitImpl(AstPartNode nd) throws DataTagsRuntimeException {
        if (nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
        addIds(nd.getAstNodes());
    }

    @Override
    public void visitImpl(AstContinueNode nd) throws DataTagsRuntimeException {
        if(nd.getId() == null) {
            nd.setId(nodeIdProvider.nextId());
        }
    }
    
    
}
