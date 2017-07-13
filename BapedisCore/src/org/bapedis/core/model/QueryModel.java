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
import org.neo4j.graphdb.Label;
import org.openide.nodes.Node;

/**
 *
 * @author loge
 */
public class QueryModel {

    public static final String ADDED_LABEL = "ADDED_LABEL";
    public static final String REMOVED_LABEL = "REMOVED_LABEL";
    public static final String ADDED_METADATA = "ADDED_METADATA";
    public static final String REMOVED_METADATA = "REMOVED_METADATA";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected final Node rootContext;
    private final List<Label> labels;
    private final List<Metadata> metadatas;

    public QueryModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        rootContext = new QueryNode(this);
        labels = new LinkedList<>();
        metadatas = new LinkedList<>();
    }

    public Label[] getLabels() {
        return labels.toArray(new Label[0]);
    }

    public Metadata[] getMetadatas() {
        return metadatas.toArray(new Metadata[0]);
    }

    public void add(Label label) {
        if (!contains(label)) {
            labels.add(label);
            propertyChangeSupport.firePropertyChange(ADDED_LABEL, null, label);
        }
    }

    public boolean contains(Label label) {
        for (Label l : labels) {
            if (l.name().equals(label.name())) {
                return true;
            }
        }
        return false;
    }

    public void remove(Label label) {
        labels.remove(label);
        propertyChangeSupport.firePropertyChange(REMOVED_LABEL, label, null);
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

    public Node getRootContext() {
        return rootContext;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}
