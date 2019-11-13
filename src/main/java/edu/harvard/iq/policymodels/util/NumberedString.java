package edu.harvard.iq.policymodels.util;

import java.util.Objects;

/**
 * A String with a number attached to it. Normally used to locate lines in a file.
 * @author michael
 */
public class NumberedString {
    public final String string;
    public final int number;

    public NumberedString(String string, int number) {
        this.string = string;
        this.number = number;
    }
    
    public NumberedString copy( String newString ) {
        return new NumberedString(newString, number);
    }
    
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 13 * hash + this.number;
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
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NumberedString other = (NumberedString) obj;
        if (this.number != other.number) {
            return false;
        }
        return Objects.equals(this.string, other.string);
    }

    @Override
    public String toString() {
        return "[NumberedString" + number + ":" + string +']';
    }
    
    
}
