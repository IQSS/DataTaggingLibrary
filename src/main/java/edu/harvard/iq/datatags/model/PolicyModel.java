package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.inference.AbstractValueInferrer;
import edu.harvard.iq.datatags.model.slots.CompoundSlot;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * The Main reason we're here - a model of a policy. Holds metadata, decision graph,
 * and a policy space. Can locate localized information when requested.
 * 
 * @author michael
 */
public class PolicyModel {
    
    private PolicyModelData metadata;
    private CompoundSlot spaceRoot;
    private DecisionGraph decisionGraph;
    private final Set<String> localizations = new TreeSet<>();
    private Set<AbstractValueInferrer> valueInferrers = new HashSet<>();

    
    public PolicyModelData getMetadata() {
        return metadata;
    }

    public void setMetadata(PolicyModelData metadata) {
        this.metadata = metadata;
    }

    public CompoundSlot getSpaceRoot() {
        return spaceRoot;
    }

    public void setSpaceRoot(CompoundSlot aSpaceRoot) {
        spaceRoot = aSpaceRoot;
    }

    public DecisionGraph getDecisionGraph() {
        return decisionGraph;
    }

    public void setDecisionGraph(DecisionGraph decisionGraph) {
        this.decisionGraph = decisionGraph;
    }
    
    public void addLocalization( String locName ) {
        localizations.add(locName);
    }

    public Set<String> getLocalizations() {
        return localizations;
    }
    
    public Path getDirectory() {
        return ( metadata.getMetadataFile() != null ) ? metadata.getMetadataFile().getParent() : null;
    }
    
    public Set<AbstractValueInferrer> getValueInferrers() {
        return valueInferrers;
    }

    public void setValueInferrers(Set<AbstractValueInferrer> valueInferrers) {
        this.valueInferrers = valueInferrers;
    }
    
    @Override
    public String toString() {
        return "[PolicyMode path:" + getMetadata().getMetadataFile().toString() + "]";
    }
    
}