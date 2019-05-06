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
    
    private final Map<AbstractSlot, LocalizationTexts> slotsTexts = new HashMap<>();
    
    private final Map<AbstractValue, LocalizationTexts> slotValuesTexts = new HashMap<>();
    
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
    
    public void setSlotTexts( AbstractSlot st, LocalizationTexts texts ) {
//        if ( text.trim().length() > 0 ) {
            slotsTexts.put(st, texts);
//        }
    }
    
    public Optional<LocalizationTexts> getSlotText( AbstractSlot st ) {
        return Optional.ofNullable(slotsTexts.get(st));
    }
    
    public void setSlotValueTexts( AbstractValue tv, LocalizationTexts texts ) {
//        if ( text.trim().length() > 0 ) {
            slotValuesTexts.put(tv, texts);
//        }
    }
    
    public Optional<LocalizationTexts> getSlotValueText( AbstractValue tv ) {
        return Optional.ofNullable(slotValuesTexts.get(tv));
    }

    public String getDirection() {
        return localizedModelData.getDirection();
    }

    
    public boolean isRtl() {
        return getDirection()!=null && getDirection().equals("rtl");
    }
    
    @Override
    public String toString() {
        return "[Localization language:" + getLanguage() + ']';
    }
    
}