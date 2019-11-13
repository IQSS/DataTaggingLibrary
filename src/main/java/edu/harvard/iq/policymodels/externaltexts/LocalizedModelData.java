package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.model.metadata.BaseModelData;
import edu.harvard.iq.policymodels.model.metadata.PolicyModelData;

/**
 * The localizable fields of a {@link PolicyModelData}.
 * 
 * @author michael
 */
public class LocalizedModelData extends BaseModelData {
    
    private String language;
    
    private String direction = "rtl";
    
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
    
    public void setUiLanguage(String aUiLang) {
        uiLang = aUiLang;
        if ( uiLang!=null ) {
            uiLang = uiLang.toLowerCase();
        }
    }
    
    public String getUiLanguage() {
        return uiLang;
    }
    
}
