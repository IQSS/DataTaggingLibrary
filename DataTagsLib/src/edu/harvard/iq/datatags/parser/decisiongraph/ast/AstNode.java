package edu.harvard.iq.datatags.parser.decisiongraph.ast;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * Base class for instruction nodes, which are the top-level nodes in decision
 * graphs.
 *
 * @author Michael Bar-Sinai
 */
public abstract class AstNode {

    public interface Visitor<T> {

        T visit(AstConsiderNode astNode);

        T visit(AstAskNode astNode);

        T visit(AstCallNode astNode);

        T visit(AstEndNode astNode);

        T visit(AstSetNode astNode);

        T visit(AstRejectNode astNode);

        T visit(AstTodoNode astNode);
        
        T visit (AstSectionNode astNode);
    }

    public static abstract class NullVisitor implements Visitor<Void> {


        @Override
        public Void visit(AstConsiderNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstAskNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstSetNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstRejectNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstCallNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstTodoNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AstEndNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }
        
        @Override
        public Void visit(AstSectionNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        
        public abstract void visitImpl(AstConsiderNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstAskNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstSetNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstRejectNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstCallNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstTodoNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AstEndNode nd) throws DataTagsRuntimeException;
        
        public abstract void visitImpl(AstSectionNode nd) throws DataTagsRuntimeException;

    }

    private String id;

    public AstNode(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public abstract <T> T accept(Visitor<T> v);

    @Override
    public int hashCode() {
        return 47 * Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof AstNode)) {
            return false;
        }
        return equalsAsAstNode((AstNode) obj);
    }

    public boolean equalsAsAstNode(AstNode other) {
        return Objects.equals(getId(), other.getId());
    }

    protected String toStringExtras() {
        return "";
    }

    @Override
    public String toString() {
        String[] classNameComps = getClass().getCanonicalName().split("\\.");
        return "[" + classNameComps[classNameComps.length - 1] + " id:" + getId() + " " + toStringExtras() + "]";
    }
}
