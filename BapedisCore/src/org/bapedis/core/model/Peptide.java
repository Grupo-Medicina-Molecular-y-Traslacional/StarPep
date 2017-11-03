/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import org.bapedis.core.services.ProjectManager;
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
    
    public static final PeptideAttribute ID = new PeptideAttribute("id", NbBundle.getMessage(Peptide.class, "Peptide.attribute.id"), String.class, false);
    public static final PeptideAttribute SEQ = new PeptideAttribute("seq", NbBundle.getMessage(Peptide.class, "Peptide.attribute.seq"), String.class, false);
    public static final PeptideAttribute LENGHT = new PeptideAttribute("length", NbBundle.getMessage(Peptide.class, "Peptide.attribute.length"), Integer.class, true);
    

    protected final Node graphNode;
    protected final Graph graph;
    protected HashMap<PeptideAttribute, Object> attrsValue;
    protected ProteinSequence biojavaSeq;

    public Peptide(Node graphNode, Graph graph) {
        this.graphNode = graphNode;
        this.graph = graph;
        attrsValue = new LinkedHashMap<>();
    }

    public String getId() {
        return (String) attrsValue.get(ID);
    }

    public String getSequence() {
        return (String) attrsValue.get(SEQ);
    }

    public ProteinSequence getBiojavaSeq() throws CompoundNotFoundException {
        if (biojavaSeq == null) {
            biojavaSeq = new ProteinSequence(getSequence());
        }
        return biojavaSeq;
    }

    public int getLength() {
        return (int) attrsValue.get(LENGHT);
    }

    public void setAttributeValue(PeptideAttribute attr, Object value) {
        attrsValue.put(attr, value);
    }

    public Object getAttributeValue(PeptideAttribute attr) {
        return attrsValue.get(attr);
    }

    public void deleteAttribute(PeptideAttribute attr) {
        if (attrsValue.containsKey(attr)) {
            attrsValue.remove(attr);
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
            values.add((String) node.getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
        }
        return values.toArray(new String[0]);
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
