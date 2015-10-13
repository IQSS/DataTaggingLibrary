package edu.harvard.iq.datatags.parser.decisiongraph.references;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 * Base class for instruction nodes, which are the top-level
 * nodes on flowcharts.
 * 
 * @author Michael Bar-Sinai
 */
public abstract class InstructionNodeRef extends NodeRef {
    
	public interface Visitor<T> {
		T visit( AskNodeRef askRef );
		T visit( CallNodeRef callRef );
		T visit( EndNodeRef endRef );
		T visit( SetNodeRef setRef );
		T visit( RejectNodeRef setRef );
		T visit( TodoNodeRef todoRef );
	}
    
    public static abstract class NullVisitor implements Visitor<Void> {

        @Override
        public Void visit(AskNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(SetNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(RejectNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(CallNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(TodoNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        @Override
        public Void visit(EndNodeRef nd) throws DataTagsRuntimeException {
            visitImpl(nd);
            return null;
        }

        public abstract void visitImpl( AskNodeRef nd    ) throws DataTagsRuntimeException;
        public abstract void visitImpl( SetNodeRef nd    ) throws DataTagsRuntimeException;
        public abstract void visitImpl( RejectNodeRef nd ) throws DataTagsRuntimeException;
        public abstract void visitImpl( CallNodeRef nd   ) throws DataTagsRuntimeException;
        public abstract void visitImpl( TodoNodeRef nd   ) throws DataTagsRuntimeException;
        public abstract void visitImpl( EndNodeRef nd    ) throws DataTagsRuntimeException;

    }
	
    public InstructionNodeRef( String id ) {
        super( id );
    }

	public abstract <T> T accept( Visitor<T> v );
	
    @Override
    public int hashCode() {
        return 47 * Objects.hashCode(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof InstructionNodeRef) ) {
            return false;
        }
        return equalsAsInstructionNodeRef((InstructionNodeRef) obj);
    }
    
    public boolean equalsAsInstructionNodeRef( InstructionNodeRef other ) {
        return Objects.equals(getId(), other.getId());
    }
    
    protected String toStringExtras() {
        return "";
    }
    
    @Override
    public String toString() {
        String[] classNameComps = getClass().getCanonicalName().split("\\.");
        return "[" + classNameComps[classNameComps.length-1] + " id:" + getId() + " " + toStringExtras() + "]";
    }
}
