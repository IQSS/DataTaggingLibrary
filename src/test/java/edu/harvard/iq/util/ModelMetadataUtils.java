package edu.harvard.iq.util;

import edu.harvard.iq.policymodels.model.metadata.AuthorData;
import edu.harvard.iq.policymodels.model.metadata.PersonAuthorData;

/**
 * A class for working with model metadata during tests.
 *
 * 
 * @author michael
 */
public class ModelMetadataUtils {
  
    public static AuthorData author(String name, String email, String affiliation ) {
        PersonAuthorData author = new PersonAuthorData();
        author.setName(name);
        author.setEmail(email);
        author.setAffiliation(affiliation);
        return author;
    }
    
}
