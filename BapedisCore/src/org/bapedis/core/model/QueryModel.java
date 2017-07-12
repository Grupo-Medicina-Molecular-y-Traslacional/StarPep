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

    protected final List<MyLibraryNode> nodes;
    public static final String ADDED_NODE = "ADD";
    public static final String REMOVED_NODE = "REMOVE";
    protected transient final PropertyChangeSupport propertyChangeSupport;
    protected final Node rootContext;
    private Label[] labels;
    private Metadata[] metadatas;

    public QueryModel() {
        propertyChangeSupport = new PropertyChangeSupport(this);
        nodes = new LinkedList<>();
        rootContext = new QueryNode(this);
    }

    public Label[] getLabels() {
        return labels;
    }

    public Metadata[] getMetadatas() {
        return metadatas;
    }

    public void add(MyLibraryNode node) {
        nodes.add(node);
        propertyChangeSupport.firePropertyChange(ADDED_NODE, null, node);
    }

    public void remove(MyLibraryNode node) {
        nodes.remove(node);
        propertyChangeSupport.firePropertyChange(REMOVED_NODE, node, null);
    }

    public Node getRootContext() {
        return rootContext;
    }

    public MyLibraryNode[] getQueryNodes() {
        return nodes.toArray(new MyLibraryNode[0]);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }

}