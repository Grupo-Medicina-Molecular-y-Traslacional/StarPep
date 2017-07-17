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
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class QueryModel {

    public static final String ADDED_METADATA = "ADDED_METADATA";
    public static final String REMOVED_METADATA = "REMOVED_METADATA";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected final Node rootContext;
    private final List<Metadata> metadatas;

    public QueryModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        rootContext = new QueryNode(this);
        metadatas = new LinkedList<>();
    }

    public Metadata[] getMetadatas() {
        return metadatas.toArray(new Metadata[0]);
    }

    public void add(Metadata metadata) {
        if (!contains(metadata)) {
            metadatas.add(metadata);
            metadata.setSelected(true);
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
        metadata.setSelected(false);
        propertyChangeSupport.firePropertyChange(REMOVED_METADATA, metadata, null);
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

}
