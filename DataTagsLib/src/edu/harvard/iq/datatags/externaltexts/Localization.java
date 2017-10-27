package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

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
    private final Map<String, String> answers = new HashMap<>();
    
    private LocalizedModelData localizedModelData;
    
    private final Map<String, String> nodeText = new HashMap<>();
    
    private final Map<AbstractSlot, String> slotsText = new HashMap<>();
    
    private final Map<AbstractValue, String> slotValuesText = new HashMap<>();
    
    public Localization(String language) {
        this.language = language;
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
    
    public Set<String> getLocalizedAnswers() {
        return answers.keySet();
    }
    
    void addAnswer( String answerText, String localizedAnswerText ) {
        answers.put(answerText, localizedAnswerText);
    }
    
    public String getLanguage() {
        return language;
    }
    
    void addNodeText(String nodeId, String data) {
        nodeText.put(nodeId, data);
    }
    
    public Optional<String> getNodeText(String nodeId) {
        return Optional.ofNullable(nodeText.get(nodeId));
    }
    
    public Set<String> getLocalizedNodeIds() {
        return nodeText.keySet();
    }
    
    public void setSlotText( AbstractSlot st, String text ) {
        if ( text.trim().length() > 0 ) {
            slotsText.put(st, text);
        }
    }
    
    public Optional<String> getSlotText( AbstractSlot st ) {
        return Optional.ofNullable(slotsText.get(st));
    }
    
    public void setSlotValueText( AbstractValue tv, String text ) {
        if ( text.trim().length() > 0 ) {
            slotValuesText.put(tv, text);
        }
    }
    
    public Optional<String> getSlotValueText( AbstractValue tv ) {
        return Optional.ofNullable(slotValuesText.get(tv));
    }
    
    @Override
    public String toString() {
        return "[Localization language:" + getLanguage() + ']';
    }
    
}