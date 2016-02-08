package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.TagType;
import edu.harvard.iq.datatags.model.values.AggregateValue;
import edu.harvard.iq.datatags.model.values.AtomicValue;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.model.values.TagValue;
import edu.harvard.iq.datatags.model.values.ToDoValue;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import static edu.harvard.iq.datatags.util.CollectionHelper.C;
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
    
    private static final TagValue.Visitor<String> TAG_VALUE_PRINTER = new TagValue.Visitor<String>() {
        @Override
        public String visitToDoValue(ToDoValue v) {
            return "TODO";
        }

        @Override
        public String visitAtomicValue(AtomicValue v) {
            return v.getName();
        }

        @Override
        public String visitAggregateValue(AggregateValue v) {
            throw new UnsupportedOperationException("Aggregate values should not be here");
        }

        @Override
        public String visitCompoundValue(CompoundValue aThis) {
            throw new UnsupportedOperationException("Compound values should not be here");
        }
    };
    
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
     * 
     * @param dg
     * @return A list of validation messages regarding the flow chart set.
     */
    public List<ValidationMessage> validateUnusedTags( DecisionGraph dg ) {
        QuestionnaireTagValues interviewValues = new QuestionnaireTagValues();
        Set<TagValue> usedValues = interviewValues.gatherInterviewTagValues(dg);
        
        AllTagValues allValues = new AllTagValues();
        Set<TagValue> definedValues = allValues.gatherAllTagValues(dg);
        
        definedValues.removeAll(usedValues);
        
        validationMessages.addAll( 
                definedValues.stream()
                        .map(unused -> new ValidationMessage(Level.WARNING, describe(unused)))
                        .collect(Collectors.toList()));
        
        return validationMessages;
    }
    
    private String describe( TagValue tv ) {
        TagType tt = tv.getType();
        LinkedList<String> typePath = new LinkedList<>();
        while ( tt != null ){
            typePath.addFirst(tt.getName());
            tt = tt.getParent();
        }
        typePath.remove(0);
        return String.join("/", typePath ) + ": " + tv.accept(TAG_VALUE_PRINTER);
    }
}
