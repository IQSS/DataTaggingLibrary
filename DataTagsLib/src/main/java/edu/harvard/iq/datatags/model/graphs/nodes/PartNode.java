/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.harvard.iq.datatags.model.graphs.nodes;

import edu.harvard.iq.datatags.runtime.exceptions.DataTagsRuntimeException;
import java.util.Objects;

/**
 *
 * @author mor
 */
public class PartNode extends Node{

    private String title;
    private Node startNode;
    
    public PartNode(String anId) {
        super(anId);
    }
    
    public PartNode(String id, String title) {
        super(id);
        this.title = title;
    }

    public PartNode(String title, Node startNode, String anId) {
        super(anId);
        this.title = title;
        this.startNode = startNode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Node getStartNode() {
        return startNode;
    }

    public void setStartNode(Node startNode) {
        this.startNode = startNode;
    }

    @Override
    public <R> R accept(Visitor<R> vr) throws DataTagsRuntimeException {
        return vr.visit( this );
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + Objects.hashCode(this.title);
        hash = 97 * hash + Objects.hashCode(this.startNode);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final PartNode other = (PartNode) obj;
        if (!Objects.equals(this.title, other.title)) {
            return false;
        }
        return Objects.equals(this.startNode, other.startNode);
    } 
    
    
    
}
