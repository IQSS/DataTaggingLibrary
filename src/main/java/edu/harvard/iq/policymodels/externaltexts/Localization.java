package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
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
   
    public static final String DEFAULT_LANGUAGE_NAME = "__DEFAULT__";
    
    /**
     * The localized parts of the model meta data.
     */
    private LocalizedModelData localizedModelData;
    
    /**
     * Maps answer names from the decision graph code to localized ones.
     */
    private final Map<String, String> answers = new HashMap<>();
    
    /**
     * Texts for nodes. Some nodes may not have an entry here.
     */
    private final Map<String, String> nodeText = new HashMap<>();
    
    /**
     * Texts for policy space slots. Some slots may not have an entry here,
     * i.e. were not localized.
     */
    private final Map<AbstractSlot, LocalizationTexts> slotsTexts = new HashMap<>();
    
    /**
     * Texts for policy space values. Some values may not have an entry here,
     * i.e. were not localized.
     */
    private final Map<AbstractValue, LocalizationTexts> slotValuesTexts = new HashMap<>();
    
    /**
     * Texts for each section: title, tooltip, and possible long text.
     */
    private final Map<String, LocalizationTexts> sectionTexts = new HashMap<>();
    
    public LocalizedModelData getLocalizedModelData() {
        if ( localizedModelData==null ) {
            initializeDefaultModel();
        }            
        return localizedModelData;
    }

    public void setLocalizedModelData(LocalizedModelData aLocalizedModelData) {
        if ( aLocalizedModelData != null ) {
            localizedModelData = aLocalizedModelData; 
        } else {
            initializeDefaultModel();
        }
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
        return getLocalizedModelData().getLanguage();
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
        slotsTexts.put(st, texts);
    }
    
    public Optional<LocalizationTexts> getSlotTexts( AbstractSlot st ) {
        return Optional.ofNullable(slotsTexts.get(st));
    }
    
    public void setSlotValueTexts( AbstractValue tv, LocalizationTexts texts ) {
        slotValuesTexts.put(tv, texts);
    }
    
    public Optional<LocalizationTexts> getSlotValueTexts( AbstractValue tv ) {
        return Optional.ofNullable(slotValuesTexts.get(tv));
    }
    
     public void setSectionTexts( String nodeId, LocalizationTexts texts ) {
        sectionTexts.put(nodeId, texts);
    }
    
    public Optional<LocalizationTexts> getSectionTexts( String sectionNodeId ) {
        return Optional.ofNullable(sectionTexts.get(sectionNodeId));
    }
    
    public Set<String> getLocalizedSectionIds() {
        return sectionTexts.keySet();
    }
    
    @Override
    public String toString() {
        return "[Localization language:" + getLanguage() + ']';
    }
    
    /**
     * Create a new localized model data, with default values. Sort of a 
     * Null Object Pattern, but we can't use the same static object, since
     * the model is mutable and someone might change it for the entire
     * application.
     */
    private void initializeDefaultModel() {
        localizedModelData = new LocalizedModelData();
        localizedModelData.setDirection( LocalizedModelData.Direction.LTR );
        localizedModelData.setLanguage(DEFAULT_LANGUAGE_NAME);
        localizedModelData.setTitle("");
        localizedModelData.setSubTitle("");
    }
}