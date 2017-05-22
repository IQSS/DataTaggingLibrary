package edu.harvard.iq.datatags.model.metadata;

import java.util.Objects;

/**
 * A reference a {@link PolicyModel} relies upon.
 * @author michael
 */
public class ModelReference {
    private String doi;
    private String url;
    private String title;
    private String text;

    public String getDoi() {
        return doi;
    }

    public void setDoi(String doi) {
        this.doi = doi;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.doi);
        hash = 53 * hash + Objects.hashCode(this.title);
        hash = 53 * hash + Objects.hashCode(this.text);
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
        final ModelReference other = (ModelReference) obj;
        if (!Objects.equals(this.doi, other.doi)) {
            return false;
        }
        if (!Objects.equals(this.url, other.url)) {
            return false;
        }
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return Objects.equals(this.text, other.text);
    }

    @Override
    public String toString() {
        return "[ModelReference title:" + title + ']';
    }
    
}
