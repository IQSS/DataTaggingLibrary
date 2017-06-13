package edu.harvard.iq.datatags.model;

import edu.harvard.iq.datatags.model.metadata.PolicyModelData;
import edu.harvard.iq.datatags.model.graphs.DecisionGraph;
import edu.harvard.iq.datatags.model.types.CompoundSlot;

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
    
    @Override
    public String toString() {
        return "[PolicyMode path:" + getMetadata().getMetadataFile().toString() + "]";
    }
    
}