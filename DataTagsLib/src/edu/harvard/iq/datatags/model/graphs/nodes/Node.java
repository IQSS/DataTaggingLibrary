package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import org.codehaus.jparsec.internal.util.Objects;

/**
 * An atomic part of the program - the equivalent of a single instruction in
 * machine code.
 *
 * @author michael
 */
public abstract class Node {

    public interface Visitor<R> {

        R visit(ConsiderNode nd) throws DataTagsRuntimeException;

        R visit(AskNode nd) throws DataTagsRuntimeException;

        R visit(SetNode nd) throws DataTagsRuntimeException;

        R visit(RejectNode nd) throws DataTagsRuntimeException;

        R visit(CallNode nd) throws DataTagsRuntimeException;

        R visit(ToDoNode nd) throws DataTagsRuntimeException;

        R visit(EndNode nd) throws DataTagsRuntimeException;
    }

    public static abstract class VoidVisitor implements Visitor<Void> {

        @Override
        public Void visit(ConsiderNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(AskNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(SetNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(RejectNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(CallNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(ToDoNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(EndNode nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        public abstract void visitImpl(ConsiderNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(AskNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(SetNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(RejectNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(CallNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(ToDoNode nd) throws DataTagsRuntimeException;

        public abstract void visitImpl(EndNode nd) throws DataTagsRuntimeException;

    }

    private final String id;

    public Node(String anId) {
        id = anId;
    }

    public abstract <R> R accept(Node.Visitor<R> vr) throws DataTagsRuntimeException;

    
    @Override
    public String toString() {
        String comps[] = getClass().getName().split("\\.");
        String toStringExtras = toStringExtras();
        if (!toStringExtras.isEmpty()) {
            toStringExtras = " " + toStringExtras;
        }
        return String.format("[%s id:%s%s]",
                comps[comps.length - 1], getId(), toStringExtras);
    }

    protected String toStringExtras() {
        return "";
    }

    public String getId() {
        return id;
    }

    protected boolean equalsAsNode(Node otherNode) {
        return Objects.equals(getId(), otherNode.getId());
    }

}
