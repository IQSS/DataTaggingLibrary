package edu.harvard.iq.datatags.runtime;

import java.net.URL;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

/**
 * A serializable capture of the state of a runtime engine. Used to allow
 * state storage and restoration.
 * 
 * @author michael
 */
public class RuntimeEngineState implements java.io.Serializable {
    
    private RuntimeEngine.Status status;
    private URL flowchartSetSource;
    private String flowchartSetVersion;
    
    private String currentNodeId;
    private final Deque<String> stack = new LinkedList<>();
    
    private final Map<String, String> datatagsStatus = new HashMap<>();

    public RuntimeEngine.Status getStatus() {
        return status;
    }

    public Deque<String> getStack() {
        return stack;
    }

    public void pushNodeIdToStack( String nodeId ) {
        stack.push(nodeId);
    }
    
    public void setStatus(RuntimeEngine.Status status) {
        this.status = status;
    }

    public URL getFlowchartSetSource() {
        return flowchartSetSource;
    }

    public void setFlowchartSetSource(URL flowchartSetSource) {
        this.flowchartSetSource = flowchartSetSource;
    }

    public String getFlowchartSetVersion() {
        return flowchartSetVersion;
    }

    public void setFlowchartSetVersion(String flowchartSetVersion) {
        this.flowchartSetVersion = flowchartSetVersion;
    }

    public String getCurrentNodeId() {
        return currentNodeId;
    }

    public void setCurrentNodeId(String currentNodeId) {
        this.currentNodeId = currentNodeId;
    }
    
    public void putDatatagValue( String slotPath, String value ) {
        datatagsStatus.put(slotPath, value);
    }
    
    public String getDatatagValue( String slotPath ) {
        return datatagsStatus.get(slotPath);
    }
    
    public Set<String> getDatatagSlotPaths() {
        return datatagsStatus.keySet();
    }
    
}
