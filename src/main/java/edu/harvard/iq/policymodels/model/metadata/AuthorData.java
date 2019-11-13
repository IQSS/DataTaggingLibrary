package edu.harvard.iq.policymodels.model.metadata;

import java.util.Objects;

/**
 * Data bit about an author of the questionnaire. This may be a person or a group,
 * hence the abstract parent class.
 * 
 * @author michael
 */
public abstract class AuthorData {
    
    protected String url;
    protected String name;
    protected String email;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public interface Visitor<R> {
        public R visit( PersonAuthorData p );
        public R visit( GroupAuthorData  g );
    }
            
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
    
    public abstract String displayString();
        
    public abstract <R> R accept(Visitor<R> v);

    protected boolean equals(AuthorData other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        return Objects.equals(this.url, other.url)
                && Objects.equals(this.name, other.name);
    }
    
    
}
