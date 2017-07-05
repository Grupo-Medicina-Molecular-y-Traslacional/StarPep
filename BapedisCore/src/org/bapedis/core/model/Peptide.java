/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import org.bapedis.core.spi.data.PeptideDAO;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class Peptide {
    protected final Node graphNode;
    protected final Graph graph;
    protected HashMap<PeptideAttribute, Object> attrsValue;

    public Peptide(Node graphNode, Graph graph) {
        this.graphNode = graphNode;
        this.graph = graph;
        attrsValue = new LinkedHashMap<>();
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
    }

    public Object getAttributeValue(PeptideAttribute attr) {
        return attrsValue.get(attr);
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
    

    @Override
    public String toString() {
        return getSequence();
    }

}
