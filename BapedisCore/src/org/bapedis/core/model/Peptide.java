/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.bapedis.core.project.ProjectManager;
import org.biojava.nbio.core.exceptions.CompoundNotFoundException;
import org.biojava.nbio.core.sequence.ProteinSequence;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
 public class Peptide {

    public static final PeptideAttribute ID = new PeptideAttribute("id", NbBundle.getMessage(Peptide.class, "Peptide.attribute.id"), Integer.class, true);
    public static final PeptideAttribute SEQ = new PeptideAttribute("seq", NbBundle.getMessage(Peptide.class, "Peptide.attribute.seq"), String.class, true);
    public static final MolecularDescriptor LENGHT = new MolecularDescriptor("length", NbBundle.getMessage(Peptide.class, "Peptide.attribute.length"), Integer.class);
    public final static String CHANGED_ATTRIBUTE = "changed_attribute";
    protected final PropertyChangeSupport propertyChangeSupport;

    protected final Node graphNode;
    protected final Graph graph;
    protected Map<PeptideAttribute, Object> attrsValue;

    protected int id;
    protected String seq;
    protected ProteinSequence biojavaSeq;    
    protected int length;

    public Peptide(Node graphNode, Graph graph) {
        this.graphNode = graphNode;
        this.graph = graph;
        attrsValue = Collections.synchronizedMap(new LinkedHashMap<>());
        propertyChangeSupport = new PropertyChangeSupport(this);
        
        id = -1;
        seq = null;
        length = 0;
    }

    public int getId() {
        if (id == -1){
            id = (int) attrsValue.get(ID);
        }
        return id;
    }
    
    public String getName(){
        return (String)graphNode.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME);
    }

    public String getSequence() {
        if (seq == null){
            seq = (String) attrsValue.get(SEQ);
        }
        return seq;
    }

    public ProteinSequence getBiojavaSeq() throws CompoundNotFoundException {
        if (biojavaSeq == null) {
            biojavaSeq = new ProteinSequence(getSequence());
        }
        return biojavaSeq;
    }

    public int getLength() {
        if (length == 0){
            length = (int) attrsValue.get(LENGHT); 
        }
        return length;
    }

    public void setAttributeValue(PeptideAttribute attr, Object value) {
        attrsValue.put(attr, value);
        propertyChangeSupport.firePropertyChange(CHANGED_ATTRIBUTE, null, attr);
    }

    public Object getAttributeValue(PeptideAttribute attr) {
        Object val = attrsValue.get(attr);
        return (val != null) ? val: attr.getDefaultValue();
    }

    public void deleteAttribute(PeptideAttribute attr) {
        if (attrsValue.containsKey(attr)) {
            attrsValue.remove(attr);
            propertyChangeSupport.firePropertyChange(CHANGED_ATTRIBUTE, attr, null);
        }
    }
    
    public boolean hasAttribute(PeptideAttribute attr){
        return attrsValue.containsKey(attr);
    }

    public PeptideAttribute[] getAttributes() {
        return attrsValue.keySet().toArray(new PeptideAttribute[0]);
    }

    public Node getGraphNode() {
        return graphNode;
    }

    public Graph getGraph() {
        return graph;
    }

    public NodeIterable getNeighbors(StarPepAnnotationType aType) {
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return relType != -1 ? graph.getNeighbors(graphNode, relType) : new NodeIterable.NodeIterableEmpty();
    }

    public Edge getEdge(Node neighbor, StarPepAnnotationType aType) {
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return relType != -1 ? graph.getEdge(graphNode, neighbor, relType) : null;
    }

    public String[] getAnnotationValues(StarPepAnnotationType aType) {
        NodeIterable neighbors = getNeighbors(aType);
        List<String> values = new LinkedList<>();
        for (Node node : neighbors) {
            values.add((String) node.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
        }
        return values.toArray(new String[0]);
    }

    public void addMolecularFeatureChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(CHANGED_ATTRIBUTE, listener);
    }

    public void removeMolecularFeatureChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(CHANGED_ATTRIBUTE, listener);
    }          

    @Override
    public String toString() {
        return getSequence();
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 37 * hash + Objects.hashCode(getId());
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
        final Peptide other = (Peptide) obj;
        return Objects.equals(this.getId(), other.getId());
    }

}
