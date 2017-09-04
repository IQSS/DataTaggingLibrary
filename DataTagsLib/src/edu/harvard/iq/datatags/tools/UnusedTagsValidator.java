package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.PolicyModel;
import edu.harvard.iq.datatags.model.values.AbstractValue;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Checks that all tag values are used in set nodes.
 * 
 * Returns a WARNING and the name of the tag type
 * containing the unused tag value.
 * 
 * @author Naomi
 */
public class UnusedTagsValidator {
    
    private final List<ValidationMessage> validationMessages = new LinkedList<>();

    /** 
     * Collect all used atomic values, and see which are defined in the tagspace 
     * but are not used.
     * 
     * @param pm the decision graph to inspect.
     * @return A list of validation messages regarding the flow chart set.
     */
    public List<ValidationMessage> validateUnusedTags( PolicyModel pm ) {
        QuestionnaireTagValues interviewValues = new QuestionnaireTagValues();
        Set<AbstractValue> usedValues = interviewValues.gatherInterviewTagValues(pm.getDecisionGraph());
        
        AllTagValues allValues = new AllTagValues();
        Set<AbstractValue> definedValues = allValues.gatherAllTagValues(pm.getSpaceRoot());
        
        definedValues.removeAll(usedValues);
        
        validationMessages.addAll( 
                definedValues.stream().map(
                        unused -> new ValidationMessage(Level.WARNING, unused.toString())).collect(Collectors.toList()));
        
        return validationMessages;
    }
    
}
