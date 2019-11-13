package edu.harvard.iq.policymodels.runtime;

import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Objects;

/**
 * A serializable capture of the state of a runtime engine. Used to allow
 * state storage and restoration.
 * 
 * @author michael
 */
public class RuntimeEngineState implements java.io.Serializable {
    
    private RuntimeEngineStatus status;
    private URL decisionGraphSetSource;
    private String decisionGraphSetVersion;
    
    private String currentNodeId;
    private final Deque<String> stack = new LinkedList<>();
    
    private Map<String, String> serializedTagValue = new HashMap<>();

    public RuntimeEngineStatus getStatus() {
        return status;
    }

    public Deque<String> getStack() {
        return stack;
    }

    public void pushNodeIdToStack( String nodeId ) {
        stack.push(nodeId);
    }
    
    public void setStatus(RuntimeEngineStatus status) {
        this.status = status;
    }

    public URL getDecisionGraphSetSource() {
        return decisionGraphSetSource;
    }

    public void setDecisionGraphSetSource(URL decisionGraphSetSource) {
        this.decisionGraphSetSource = decisionGraphSetSource;
    }

    public String getDecisionGraphSetVersion() {
        return decisionGraphSetVersion;
    }

    public void setDecisionGraphSetVersion(String decisionGraphSetVersion) {
        this.decisionGraphSetVersion = decisionGraphSetVersion;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }

    public Map<String, String> getSerializedTagValue() {
        return serializedTagValue;
    }

    public void setSerializedTagValue(Map<String, String> serializedTagValue) {
        this.serializedTagValue = serializedTagValue;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 47 * hash + Objects.hashCode(this.status);
        hash = 47 * hash + Objects.hashCode(this.currentNodeId);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final RuntimeEngineState other = (RuntimeEngineState) obj;
        if (this.status != other.status) {
            return false;
        }
        if (!Objects.equals(this.decisionGraphSetSource, other.decisionGraphSetSource)) {
            return false;
        }
        if (!Objects.equals(this.decisionGraphSetVersion, other.decisionGraphSetVersion)) {
            return false;
        }
        if (!Objects.equals(this.currentNodeId, other.currentNodeId)) {
            return false;
        }
        if (!Objects.equals(this.stack, other.stack)) {
            return false;
        }
        return Objects.equals(this.serializedTagValue, other.serializedTagValue);
    }
    
    
    
}
