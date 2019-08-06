package edu.harvard.iq.policymodels.externaltexts;

import java.util.Objects;

/**
 * A string in a given markup format.
 * @author michael
 */
public class MarkupString {
    
    private final MarkupFormat format;
    private final String content;

    public MarkupString(MarkupFormat format, String content) {
        this.format = format;
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public MarkupFormat getFormat() {
        return format;
    }
    
    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.format);
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
        final MarkupString other = (MarkupString) obj;
        if (!Objects.equals(this.content, other.content)) {
            return false;
        }
        return this.format == other.format;
    }

    @Override
    public String toString() {
        return "[MarkupString format:" + format + " content:" + content + ']';
    }
    
}
