package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.charts.FlowChartSet;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

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
     * In InterviewTagValues, I get all of the
     * TagTypes held in the CompoundValue in each SetNode and add
     * them to the set, but I think it is not counting correctly.
     * Some of the warnings of unused tags are correct, and they
     * are not in the questionnaire. Others are there, however,
     * which makes me think that either the subtraction of
     * interview tag values from all tag values is incorrect,
     * or the counting of interview tag values is incorrect.
     * Since I tried another method of subtracting tag values (going
     * through all tag values in a loop and only adding tag
     * values to a set of unusedValues when they were not found
     * in the interview tag values) and this came up with the same
     * list of unused tags, I believe the counting of interview
     * tag values is incorrect.
     * 
     * In InterviewTagValues I simply add all TagValues in the
     * CompoundValue of each SetNode to the list of usedValues,
     * which is different from how I add the TagValues in
     * AllTagValues (there, I only add the values in the SimpleType
     * and the TodoType) -- in InterviewTagValues I could be
     * counting CompoundValues and AggregateValues as well as
     * SimpleValues and TodoValues.
     */
    
    public List<ValidationMessage> validateUnusedTags(FlowChartSet fcs) {
        InterviewTagValues interviewValues = new InterviewTagValues();
        Set<TagValue> usedValues = interviewValues.gatherInterviewTagValues(fcs);
        System.out.println("Interview tag values: " + usedValues.size());
        
        AllTagValues allValues = new AllTagValues();
        Set<TagValue> definedValues = allValues.gatherAllTagValues(fcs);
        System.out.println("All tag values: " + definedValues.size());
        
        
        definedValues.removeAll(usedValues);
        System.out.println("Unused tag values: " + definedValues.size());
        
        for (TagValue unused : definedValues) {
            validationMessages.add(new ValidationMessage(Level.WARNING, unused.toString()));
        }
        
        return validationMessages;
    }
    
}
