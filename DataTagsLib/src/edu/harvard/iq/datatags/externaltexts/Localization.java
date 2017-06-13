package edu.harvard.iq.datatags.externaltexts;

import java.util.EnumMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * A localization of a policy model to a given language.
 * 
 * @author michael
 */
public class Localization {
   
    /**
     * Language the localization is in. Does not have to be an ISO code.
     */
    private final String language;
    
    /**
     * Maps answer names from the decision graph code to localized ones.
     */
    private final Map<String, String> answers = new TreeMap<>();
    
    private LocalizedModelData localizedModelData;
    
    private Map<FileFormat,String> readmes = new EnumMap<>(FileFormat.class);

    public Localization(String language) {
        this.language = language;
    }
    
    /**
     * Removes versions of localization data from {@code this}, based on the passed format. Useful for server situations,
     * where it makes sense to remove all versions that will not be used.
     * 
     * @param bestQuality Quality above which versions can be removed.
     */
    public void purge(FileFormat bestQuality) {
        // TODO implement
    }
    
    public LocalizedModelData getLocalizedModelData() {
        return localizedModelData;
    }

    public void setLocalizedModelData(LocalizedModelData localizedModelData) {
        this.localizedModelData = localizedModelData;
    }
    
    public String localizeAnswer( String dgAnswer ) {
        return answers.getOrDefault(dgAnswer, dgAnswer);
    }
    
    void addAnswer( String answerText, String localizedAnswerText ) {
        answers.put(answerText, localizedAnswerText);
    }
    
    public String getLanguage() {
        return language;
    }
    
    @Override
    public String toString() {
        return "[Localization language:" + getLanguage() + ']';
    }
}
