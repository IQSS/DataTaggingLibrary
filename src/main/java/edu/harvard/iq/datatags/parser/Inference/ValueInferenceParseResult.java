package edu.harvard.iq.datatags.parser.Inference;

import edu.harvard.iq.datatags.model.inference.AbstractValueInferrer;
import edu.harvard.iq.datatags.model.inference.AbstractValueInferrer.InferencePair;
import edu.harvard.iq.datatags.model.inference.ComplianceValueInferrer;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import edu.harvard.iq.datatags.model.values.CompoundValue;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst;
import edu.harvard.iq.datatags.parser.decisiongraph.ValueBuilder;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AstSetNode;
import edu.harvard.iq.datatags.model.inference.SupportValueInferrer;
import edu.harvard.iq.datatags.model.slots.AbstractSlot;
import edu.harvard.iq.datatags.model.values.AbstractValue.CompareResult;
import edu.harvard.iq.datatags.parser.Inference.ast.ValueInferrerAst.InferencePairAst;
import edu.harvard.iq.datatags.parser.decisiongraph.ast.AtomicSlotValuePair;
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
    private final Set<AbstractValueInferrer> inferences = new HashSet<>();
    
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
    
    public Set<AbstractValueInferrer> buildValueInference() {
        inferencesAst.forEach(i -> {
            // For each ValueInferrereAst
            AbstractValueInferrer valueInferrer = null;
            switch (i.getType()) {
                case "copmly": valueInferrer = new ComplianceValueInferrer(); break;
                case "support": valueInferrer = new SupportValueInferrer(); break;
                default: validationMessages.add(new ValidationMessage(Level.ERROR, "Unknown inferrer type '" + i.getType() + "'."));
            }
            if ( valueInferrer != null ) {
            for ( InferencePairAst pair : i.getInferencePairs() ) {
                    // For each pair
                    final CompoundValue minimalCoordinates = topLevelType.createInstance();
                    ValueBuilder valueBuilder = new ValueBuilder(minimalCoordinates, fullyQualifiedSlotName);
                    try {
                        pair.getMinimalCoordinate().forEach(asnmnt -> asnmnt.accept(valueBuilder));
                    } catch (RuntimeException re) {
                        validationMessages.add(new ValidationMessage(Level.ERROR, re.getMessage() + " at value " + i.toStringSlotName()));
                    }
                    // minimalCoordinates -> CompoundValue from all minimalCoordinates

                    AtomicSlotValuePair asnmnt = new AtomicSlotValuePair
                                                                (i.getSlot(), pair.getInferredValue());
                    final CompoundValue inferredValue = topLevelType.createInstance();
                    ValueBuilder valueBuilderForInferredValue = new ValueBuilder
                                                                (inferredValue, fullyQualifiedSlotName);
                    asnmnt.accept(valueBuilderForInferredValue);

                    // inferredValue -> compoundValue from the slotName and inferred value
                    Boolean isOk = true;
                    if (!valueInferrer.getInferencePairs().isEmpty()){
                        CompoundValue lastMinimalCoorinate = valueInferrer.getInferencePairs()
                                .get(valueInferrer.getInferencePairs().size() - 1).getMinimalCoordinate();
                        Set<AbstractSlot> minimalCoordNonEmptySlots = minimalCoordinates.getNonEmptySubSlots();
                        
                        isOk = lastMinimalCoorinate.project(minimalCoordNonEmptySlots).isEmpty() ? false
                                : minimalCoordinates.project(lastMinimalCoorinate.getNonEmptySubSlots()).compare(lastMinimalCoorinate.project(minimalCoordNonEmptySlots))==CompareResult.Bigger;
                        if ( !isOk ) {
                            validationMessages.add(new ValidationMessage(Level.ERROR,
                                    "Slots are not ordered hierarchically - " + lastMinimalCoorinate.toString() + " and " + minimalCoordinates));
                        }
                    }
                    if ( isOk ) {
                        InferencePair inferencePair = new InferencePair(minimalCoordinates, inferredValue);
                        valueInferrer.add(inferencePair);
                    }
                }
            }
            
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
