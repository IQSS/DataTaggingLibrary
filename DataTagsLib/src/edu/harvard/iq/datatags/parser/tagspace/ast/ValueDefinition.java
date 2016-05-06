package edu.harvard.iq.datatags.parser.tagspace.ast;

import java.util.Objects;

/**
 * Definition of a single value: its name, and a possible note.
 * @author michael
 */
public class ValueDefinition {
    
    private final String name;
    
    private final String note;

    public ValueDefinition(String name, String note) {
        this.name = name;
        this.note = note;
    }

    public String getName() {
        return name;
    }

    public String getNote() {
        return note;
    }
    
    @Override
    public String toString() {
        return "[ValueDefinition name:" + getName() 
                + ((note.length() > 0 ) ? " note:" + getNote() : "" ) 
                + "]";
    }
    
    @Override
    public int hashCode() {
        return name.hashCode();
    }
    
    @Override
    public boolean equals( Object other ) {
        if ( other == null ) return false;
        if ( other == this ) return true;
        
        if ( other instanceof ValueDefinition ) {
            ValueDefinition otherVD = (ValueDefinition) other;
            return Objects.equals(getName(), otherVD.getName())
                    && Objects.equals(getNote(), otherVD.getNote());
            
        } else {
            return false;
        }
    }
    
}
