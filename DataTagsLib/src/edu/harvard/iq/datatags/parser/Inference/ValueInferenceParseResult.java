package edu.harvard.iq.datatags.parser.Inference;

import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.decisiongraph.SetNodeValueBuilder;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.model.ValueInferrer;
import edu.harvard.iq.datatags.model.ValueInferrer.InferencePair;
import edu.harvard.iq.datatags.tools.ValidationMessage;
import edu.harvard.iq.datatags.tools.ValidationMessage.Level;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author mor_vilozni
 */
public class ValueInferenceParseResult {
    private final List<ValueInferrerAst> inferencesAst;
    private final List<ValidationMessage> validationMessages = new LinkedList<>();
    private Map<String, ValueInferrerAst> valueByName = new HashMap<>();
    private final Set<ValueInferrer> inferences = new HashSet<>();
    
    private final Map<List<String>, List<String>> fullyQualifiedSlotName;
    private final CompoundSlot topLevelType;

    ValueInferenceParseResult( List<ValueInferrerAst> inferences, Map<List<String>, List<String>> fullyQualifiedSlotName, 
            CompoundSlot topLevelType) {
        this.inferencesAst = inferences;
        this.fullyQualifiedSlotName = fullyQualifiedSlotName;
        this.topLevelType = topLevelType;
        
        Map<String, List<ValueInferrerAst>> slotMap = new HashMap<>();
        inferences.forEach( s -> slotMap.computeIfAbsent(s.toStringSlotName(), n -> new LinkedList<>()).add(s));
        //Chack if there are duplicate slots
        Set<String> duplicates = slotMap.values().stream()
                    .filter( l -> l.size()>1 )
                    .map( l -> l.get(0).toStringSlotName())
                    .collect(Collectors.toSet());
        if ( duplicates.isEmpty()) {
            slotMap.values().stream()
                    .map ( l -> l.get(0) )
                    .forEach( v -> valueByName.put(v.toStringSlotName(), v));
        } else {
            duplicates.forEach(d -> validationMessages.add(new ValidationMessage(Level.ERROR, "ValueInference " + d + " is duplicated")));
        }
    }
    
    public Set<ValueInferrer> buildValueInference() {
        inferencesAst.forEach(i -> {
            // For each ValueInferrereAst
            ValueInferrer valueInferrer = new ValueInferrer();
            i.getInferencePairs().forEach(pair -> {
                // For each pair
                final CompoundValue minimalCoordinates = topLevelType.createInstance();
                SetNodeValueBuilder valueBuilder = new SetNodeValueBuilder(minimalCoordinates, fullyQualifiedSlotName);
                try {
                    pair.getMinimalCoordinate().forEach(asnmnt -> asnmnt.accept(valueBuilder));
                } catch (RuntimeException re) {
                    validationMessages.add(new ValidationMessage(Level.ERROR, re.getMessage() + " at value " + i.toStringSlotName()));
                }
                // minimalCoordinates -> CompoundValue from all minimalCoordinates
                
                AstSetNode.AtomicAssignment asnmnt = new AstSetNode.AtomicAssignment
                                                            (i.getSlot(), pair.getInferredValue());
                final CompoundValue inferredValue = topLevelType.createInstance();
                SetNodeValueBuilder valueBuilderForInferredValue = new SetNodeValueBuilder
                                                            (inferredValue, fullyQualifiedSlotName);
                asnmnt.accept(valueBuilderForInferredValue);
                // inferredValue -> compoundValue from the slotName and inferred value
                Boolean isOk = true;
                if (!valueInferrer.getInferencePairs().isEmpty()){
                    CompoundValue lastMinimalCoorinate = valueInferrer.getInferencePairs()
                            .get(valueInferrer.getInferencePairs().size() - 1).getMinimalCoordinate();
                    isOk = lastMinimalCoorinate.project(minimalCoordinates.getNonEmptySubSlots()).isEmpty() ? false
                            : minimalCoordinates.project(lastMinimalCoorinate.getNonEmptySubSlots()).isBigger(lastMinimalCoorinate.project(minimalCoordinates.getNonEmptySubSlots())).orElse(false);
                    if ( !isOk ) {
                        validationMessages.add(new ValidationMessage(Level.ERROR,
                                "Slots are not ordered hierarchically - " + lastMinimalCoorinate.toString() + " and " + minimalCoordinates));
                    }
                }
                if ( isOk ) {
                    InferencePair inferencePair = new InferencePair(minimalCoordinates, inferredValue);
                    valueInferrer.add(inferencePair);
                }
            });
            
            if ( isValid() ) {
                inferences.add(valueInferrer);
            }
        });
        return isValid() ? inferences : null;
    }
    
    public Boolean isValid(){
        return validationMessages.stream().noneMatch( m->m.getLevel()==ValidationMessage.Level.ERROR );
    }

    public List<ValidationMessage> getValidationMessages() {
        return validationMessages;
    }
    
}
