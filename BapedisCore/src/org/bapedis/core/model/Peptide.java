/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.spi.data.PeptideDAO;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;

/**
 *
 * @author loge
 */
public class Peptide {

    protected final Node graphNode;
    protected final Graph graph;
    protected HashMap<PeptideAttribute, Object> attrsValue;
    public final static String DESCRIPTOR_CHANGE = "descriptor_change";
    protected transient final PropertyChangeSupport propertyChangeSupport;

    public Peptide(Node graphNode, Graph graph) {
        this.graphNode = graphNode;
        this.graph = graph;
        attrsValue = new LinkedHashMap<>();
        propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public String getId() {
        return (String) attrsValue.get(PeptideDAO.ID);
    }

    public String getSequence() {
        return (String) attrsValue.get(PeptideDAO.SEQ);
    }

    public int getLength() {
        return (int) attrsValue.get(PeptideDAO.LENGHT);
    }

    public void setAttributeValue(PeptideAttribute attr, Object value) {
        attrsValue.put(attr, value);
        propertyChangeSupport.firePropertyChange(DESCRIPTOR_CHANGE, null, attr);
    }

    public Object getAttributeValue(PeptideAttribute attr) {
        return attrsValue.get(attr);
    }

    public void deleteAttribute(PeptideAttribute attr) {
        if (attrsValue.containsKey(attr)) {
            attrsValue.remove(attr);
            propertyChangeSupport.firePropertyChange(DESCRIPTOR_CHANGE, attr, null);
        }

    }

    public Set<PeptideAttribute> getAttributes() {
        return attrsValue.keySet();
    }

    public Node getGraphNode() {
        return graphNode;
    }

    public Graph getGraph() {
        return graph;
    }

    public NodeIterable getNeighbors(AnnotationType aType) {
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return relType != -1 ? graph.getNeighbors(graphNode, relType) : new NodeIterable.NodeIterableEmpty();
    }

    public Edge getEdge(Node neighbor, AnnotationType aType) {
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return relType != -1 ? graph.getEdge(graphNode, neighbor, relType) : null;
    }

    public String[] getAnnotationValues(AnnotationType aType) {
        NodeIterable neighbors = getNeighbors(aType);
        List<String> values = new LinkedList<>();
        for (Node node : neighbors) {
            values.add((String) node.getAttribute("name"));
        }
        return values.toArray(new String[0]);
    }

    @Override
    public String toString() {
        return getSequence();
    }

    public void addDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(DESCRIPTOR_CHANGE, listener);
    }

    public void removeDescriptorChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(DESCRIPTOR_CHANGE, listener);
    }

}
