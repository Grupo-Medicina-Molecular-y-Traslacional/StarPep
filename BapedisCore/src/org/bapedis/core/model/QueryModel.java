/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class QueryModel {

    public static final String ADDED_METADATA = "ADDED_METADATA";
    public static final String REMOVED_METADATA = "REMOVED_METADATA";
    public static final String CHANGED_RESTRICTION = "RESTRICTION";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected final Node rootContext;
    private final List<Metadata> metadatas;
    public static final String RUNNING = "RUNNING";
    protected final AtomicBoolean running;
    protected RestrictionLevel restriction;

    public QueryModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        rootContext = new QueryNode(this);
        metadatas = new LinkedList<>();
        running = new AtomicBoolean(false);
        restriction = RestrictionLevel.MATCH_ALL;
    }

    public RestrictionLevel getRestriction() {
        return restriction;
    }

    public void setRestriction(RestrictionLevel restriction) {
        RestrictionLevel old = this.restriction;
        this.restriction = restriction;
        propertyChangeSupport.firePropertyChange(CHANGED_RESTRICTION, old, restriction);
    }        

    public Metadata[] getMetadatas() {
        return metadatas.toArray(new Metadata[0]);
    }

    public void add(Metadata metadata) {
        if (!contains(metadata)) {
            metadatas.add(metadata);
            propertyChangeSupport.firePropertyChange(ADDED_METADATA, null, metadata);
        }
    }

    public boolean contains(Metadata metadata) {
        for (Metadata m : metadatas) {
            if (m.equals(metadata)) {
                return true;
            }
        }
        return false;
    }

    public void remove(Metadata metadata) {
        metadatas.remove(metadata);
        propertyChangeSupport.firePropertyChange(REMOVED_METADATA, metadata, null);
    }
    
    public void removeAll(){       
        metadatas.clear();
        propertyChangeSupport.firePropertyChange(REMOVED_METADATA, null, null);
    }

    public Node getRootContext() {
        return rootContext;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }
    
    public int countElements(){
        return metadatas.size();
    }
    
    public boolean isRunning() {
        return running.get();
    }

    public void setRunning(boolean running) {
        boolean oldValue = this.running.get();
        this.running.set(running);
        propertyChangeSupport.firePropertyChange(RUNNING, oldValue, running);
    }    

}
