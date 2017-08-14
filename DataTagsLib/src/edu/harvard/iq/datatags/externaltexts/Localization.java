package edu.harvard.iq.datatags.externaltexts;

import edu.harvard.iq.datatags.model.types.SlotType;
import edu.harvard.iq.datatags.model.values.TagValue;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
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
    
    private final Map<SlotType, String> slotsText = new HashMap<>();
    
    private final Map<TagValue, String> slotValuesText = new HashMap<>();
    
    /** 
     * A map of the readme files this localization can 
     */
    private final Map<MarkupFormat,MarkupString> readmes = new EnumMap<>(MarkupFormat.class);

    public Localization(String language) {
        this.language = language;
    }
    
    /**
     * Removes versions of localization data from {@code this}, based on the passed format. Useful for server situations,
     * where it makes sense to remove all versions that will not be used.
     * 
     * @param keepFormat The readme format to keep.
     */
    public void purge(MarkupFormat keepFormat) {
        Arrays.stream(MarkupFormat.values())
               .filter( f->! f.equals(keepFormat) )
                .forEach( f -> readmes.remove(f) );
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
    
    public Optional<MarkupFormat> getBestReadmeFormat() {
        return Arrays.stream(MarkupFormat.values())
                .filter( fmt -> readmes.keySet().contains(fmt) )
                .findFirst();
    }
    
    public MarkupString getReadme(MarkupFormat fmt) {
        return readmes.get(fmt);
    }
    
    void addReadme( MarkupFormat fmt, String content ) {
        readmes.put(fmt, new MarkupString(fmt,content) );
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
    
    public void setSlotText( SlotType st, String text ) {
        if ( text.trim().length() > 0 ) {
            slotsText.put(st, text);
        }
    }
    
    public Optional<String> getSlotText( SlotType st ) {
        return Optional.ofNullable(slotsText.get(st));
    }
    
    public void setSlotValueText( TagValue tv, String text ) {
        if ( text.trim().length() > 0 ) {
            slotValuesText.put(tv, text);
        }
    }
    
    public Optional<String> getSlotValueText( TagValue tv ) {
        return Optional.ofNullable(slotValuesText.get(tv));
    }
    
    @Override
    public String toString() {
        return "[Localization language:" + getLanguage() + ']';
    }
    
}