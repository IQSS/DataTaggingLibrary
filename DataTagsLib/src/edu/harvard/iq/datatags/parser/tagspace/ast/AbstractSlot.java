package edu.harvard.iq.datatags.parser.tagspace.ast;

import static edu.harvard.iq.datatags.util.CollectionHelper.C;
import java.util.Objects;

/**
 * Base class for AST nodes that describe slots.
 * @author michael
 */
public abstract class AbstractSlot {
    
    public interface Visitor<R> {
        R visit(ToDoSlot slot);
        R visit(AtomicSlot slot);
        R visit(AggregateSlot slot);
        R visit(CompoundSlot slot);
    }
    
    private final String name;
    
    private final String note;

    public AbstractSlot(String aName, String aNote) {
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
        if ( ! (obj instanceof AbstractSlot) ) {
            return false;
        }
        final AbstractSlot other = (AbstractSlot) obj;
        if (!Objects.equals(this.name, other.getName())) {
            return false;
        }
        return Objects.equals(this.note, other.getNote());
    }
    
}
