/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Set;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;

/**
 *
 * @author loge
 */
public class Peptide {

    public static final PeptideAttribute ID = new PeptideAttribute("id", "ID", String.class);
    public static final PeptideAttribute SEQ = new PeptideAttribute("seq", "Sequence", String.class);
    public static final PeptideAttribute LENGHT = new PeptideAttribute("length", "Length", Integer.class);

    protected final Node graphNode;
    protected final Graph graph;
    protected HashMap<PeptideAttribute, Object> attrsValue;

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

    public int getLength() {
        return (int) attrsValue.get(LENGHT);
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
