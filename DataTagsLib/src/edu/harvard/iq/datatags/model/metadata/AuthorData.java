package edu.harvard.iq.datatags.model.metadata;

/**
 * Data bit about an author of the questionnaire. This may be a person or a group,
 * hence the abstract parent class.
 * 
 * @author michael
 */
public interface AuthorData {
    
    interface Visitor<R> {
        public R visit( PersonAuthorData p );
        public R visit( GroupAuthorData  g );
    }
            
    String displayString();
    
    void setName( String aName );
    
    <R> R accept(Visitor<R> v);
}
