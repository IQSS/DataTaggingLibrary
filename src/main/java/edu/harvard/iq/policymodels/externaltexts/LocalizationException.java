package edu.harvard.iq.policymodels.externaltexts;

/**
 *
 * @author michael
 */
public class LocalizationException extends Exception {
    
    private final String language;

    public LocalizationException(String language, String message) {
        super(message);
        this.language = language;
    }

    public LocalizationException(String language, String message, Throwable cause) {
        super(message, cause);
        this.language = language;
    }

    public String getLanguage() {
        return language;
    }
    
}
