package edu.harvard.iq.policymodels.parser.policyspace.ast;

import static edu.harvard.iq.policymodels.util.CollectionHelper.C;
import java.util.Objects;

/**
 * Base class for AST nodes that describe slots.
 * @author michael
 */
public abstract class AbstractAstSlot {
    
    public interface Visitor<R> {
        R visit(ToDoAstSlot slot);
        R visit(AtomicAstSlot slot);
        R visit(AggregateAstSlot slot);
        R visit(CompoundAstSlot slot);
    }
    
    private final String name;
    
    private final String note;

    public AbstractAstSlot(String aName, String aNote) {
        name = aName;
        note = aNote;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }
    
    public abstract <R> R accept( Visitor<R> visitor );
    
    @Override
    public String toString() {
        String[] className = getClass().getName().split("\\.");
        return "[" + C.last(className) + ": name:" + getName() 
                + " " + toStringExtras() 
                + ((note.length() > 0 ) ? " note:" + getNote() : "" ) 
        + "]";
    }
    
    protected String toStringExtras() {
        return "";
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.name);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if ( ! (obj instanceof AbstractAstSlot) ) {
            return false;
        }
        final AbstractAstSlot other = (AbstractAstSlot) obj;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return Objects.equals(this.note, other.getNote());
    }
    
}
