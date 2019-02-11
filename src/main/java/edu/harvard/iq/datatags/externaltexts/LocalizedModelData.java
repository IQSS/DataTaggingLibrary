package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.metadata.BaseModelData;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;

/**
 * The localizable fields of a {@link PolicyModelData}.
 * @author michael
 */
public class LocalizedModelData extends BaseModelData {
    
    private String language;

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }
    
}
