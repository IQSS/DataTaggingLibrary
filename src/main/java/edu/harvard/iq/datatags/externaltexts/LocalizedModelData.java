package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.metadata.BaseModelData;
import edu.harvard.iq.datatags.model.metadata.PolicyModelData;

/**
 * The localizable fields of a {@link PolicyModelData}.
 * @author michael
 */
public class LocalizedModelData extends BaseModelData {
    
    private String language;
    
    private String direction;
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String aDirection) {
        direction = aDirection;
        if ( direction!=null ) {
            direction = direction.toLowerCase();
        }
    }
    
}
