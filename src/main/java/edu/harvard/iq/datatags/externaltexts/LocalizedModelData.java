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
    
    private String uiLang;
    
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
    
    public void setUiLang(String aUiLang) {
        uiLang = aUiLang;
        if ( uiLang!=null ) {
            uiLang = uiLang.toLowerCase();
        }
    }
    
    public String getUiLang() {
        return uiLang;
    }
    
}
