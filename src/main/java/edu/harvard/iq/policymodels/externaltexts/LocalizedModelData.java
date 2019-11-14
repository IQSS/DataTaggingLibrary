package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.model.metadata.BaseModelData;
import edu.harvard.iq.policymodels.model.metadata.PolicyModelData;
import java.util.Objects;
import java.util.Optional;

/**
 * The localizable fields of a {@link PolicyModelData}.
 * 
 * @author michael
 */
public class LocalizedModelData extends BaseModelData {
    
    public enum Direction { LTR, RTL }
    
    private String language;
    
    private Direction direction = Direction.LTR;
    
    private String uiLangnguage;
    
    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Direction getDirection() {
        return direction;
    }
    
    public void setDirection( Direction aDirection ) {
        direction = aDirection != null ? aDirection
                                       : Direction.LTR;
    }
    
    public void setUiLanguage(String aUiLang) {
        uiLangnguage = aUiLang;
    }
    
    public Optional<String> getUiLanguage() {
        return Optional.ofNullable(uiLangnguage);
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.language);
        hash = 67 * hash + Objects.hashCode(this.direction);
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
        if (! (obj instanceof LocalizedModelData) )  {
            return false;
        }
        final LocalizedModelData other = (LocalizedModelData) obj;
        
        return Objects.equals(this.language, other.language) 
               && Objects.equals(this.uiLangnguage, other.uiLangnguage)
               && this.direction == other.direction;
    }

    @Override
    public String toString() {
        return "[LocalizedModelData language=" + language 
                                + " direction=" + direction 
                                + " uiLangnguage=" + uiLangnguage + ']';
    }
    
}
