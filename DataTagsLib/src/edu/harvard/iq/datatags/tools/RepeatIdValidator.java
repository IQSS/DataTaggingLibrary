package edu.harvard.iq.datatags.tools;

import edu.harvard.iq.datatags.parser.flowcharts.references.InstructionNodeRef;
import edu.harvard.iq.datatags.parser.flowcharts.references.NodeHeadRef;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author Naomi
 */
public class RepeatIdValidator {
    
    private HashSet<String> seenIds = new HashSet<>();
    private final LinkedList<ValidationMessage> validationMessages = new LinkedList<>();
    
    public LinkedList<ValidationMessage> validateRepeatIds(List<InstructionNodeRef> refs) {
        String refId = "";
        for (InstructionNodeRef ref : refs) {
            if (seenIds.contains(ref.getId()) && ref.getId() != null) {
                validationMessages.addLast(new ValidationMessage(Level.ERROR, "Duplicate node id: \"" + ref.getId() + "\"."));
            } else {
                seenIds.add(ref.getId());
            }
        }
        return validationMessages;
    }
    
    
}