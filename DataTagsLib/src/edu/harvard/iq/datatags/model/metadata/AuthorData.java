package edu.harvard.iq.datatags.model.metadata;

/**
 * Data bit about an author of the questionnaire. This may be a person or a group,
 * hence the abstract parent class.
 * 
 * @author michael
 */
public abstract class AuthorData {
    
    protected String url;
    protected String name;

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
    
    
    public interface Visitor<R> {
        public R visit( PersonAuthorData p );
        public R visit( GroupAuthorData  g );
    }
            
    public abstract String displayString();
        
    public abstract <R> R accept(Visitor<R> v);
    
}
