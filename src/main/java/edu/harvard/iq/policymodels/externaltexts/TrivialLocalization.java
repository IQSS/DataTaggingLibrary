package edu.harvard.iq.policymodels.externaltexts;

import edu.harvard.iq.policymodels.model.PolicyModel;
import edu.harvard.iq.policymodels.model.metadata.AuthorData;
import edu.harvard.iq.policymodels.model.metadata.ModelReference;
import edu.harvard.iq.policymodels.model.policyspace.slots.AbstractSlot;
import edu.harvard.iq.policymodels.model.policyspace.values.AbstractValue;
import static edu.harvard.iq.policymodels.util.PolicySpaceHelper.name;
import static edu.harvard.iq.policymodels.util.PolicySpaceHelper.note;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * A localization whose data is based on the policy model itself, with
 * no external texts. This allows "lifting" the localization of models that do not
 * have localizations, creating a status where there is always some localization.
 * 
 * 
 * @author michael
 */
public class TrivialLocalization extends Localization {
    
    public static final String LANGUAGE_NAME = "__model__";
          
    private final PolicyModel model;
    private final LocalizedModelData modelData;
    
    public TrivialLocalization(PolicyModel pm) {
        model = pm;
        modelData = new TriviallyLocalizedModelData();
    }

    @Override
    public String toString() {
        return "[TrivialLocalization model:"+model.getMetadata().getTitle() 
                + " v" + model.getMetadata().getVersion() + "]";
    }

    @Override
    public Set<String> getLocalizedNodeIds() {
        return Collections.emptySet();
    }

    @Override
    public Optional<String> getNodeText(String nodeId) {
        return Optional.empty();
    }

    @Override
    public Set<String> getLocalizedAnswers() {
        return Collections.emptySet();
    }

    @Override
    public String localizeAnswer(String dgAnswer) {
        return dgAnswer;
    }

    @Override
    public LocalizedModelData getLocalizedModelData() {
        return modelData;
    }
    
    @Override
    public Optional<LocalizationTexts> getSlotTexts( AbstractSlot st ) {
        return Optional.of(new LocalizationTexts(st.getName(), st.getNote(), null));
    }
        
    @Override
    public Optional<LocalizationTexts> getSlotValueTexts( AbstractValue tv ) {
        return Optional.of(new LocalizationTexts(name(tv), note(tv), null));
    }

    
    protected class TriviallyLocalizedModelData extends LocalizedModelData {

        @Override
        public Optional<String> getUiLanguage() {
            return Optional.of("en");
        }

        @Override
        public Direction getDirection() {
            return Direction.LTR;
        }
         
        @Override
        public String getLanguage() {
            return LANGUAGE_NAME;
        }

        @Override
        public MarkupString getReadme(MarkupFormat fmt) {
            return model.getMetadata().getReadme(fmt);
        }

        @Override
        public Optional<MarkupFormat> getBestReadmeFormat() {
            return model.getMetadata().getBestReadmeFormat();
        }

        @Override
        public String getTitle() {
            return model.getMetadata().getTitle();
        }

        @Override
        public String getSubTitle() {
            return model.getMetadata().getSubTitle();
        }

        @Override
        public List<ModelReference> getReferences() {
            return model.getMetadata().getReferences();
        }

        @Override
        public Set<String> getKeywords() {
            return model.getMetadata().getKeywords();
        }

        @Override
        public List<AuthorData> getAuthors() {
            return model.getMetadata().getAuthors();
        }
        
    }
}
