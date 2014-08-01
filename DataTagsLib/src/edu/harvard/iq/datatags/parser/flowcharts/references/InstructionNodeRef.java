package edu.harvard.iq.datatags.parser.flowcharts.references;

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
    
    @Override
    public String toString() {
        String[] classNameComps = getClass().getCanonicalName().split("\\.");
        return "[" + classNameComps[classNameComps.length-1] + " id:" + getId() + " ]";
    }
}
