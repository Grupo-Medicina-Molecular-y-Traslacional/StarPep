/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NeoPeptide extends Peptide {

    protected final long neoId;
    protected Node graphNode;
    protected Graph graph;

    public static String getPrefixName() {
        return NbBundle.getMessage(NeoPeptide.class, "NeoPeptide.prefix");
    }

    public NeoPeptide(long neoId, Node graphNode, Graph graph) {
        this.neoId = neoId;
        this.graphNode = graphNode;
        this.graph = graph;
    }

    public long getNeoId() {
        return neoId;
    }
    
    public NodeIterable getAnnotations(AnnotationType aType){
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return graph.getNeighbors(graphNode, relType);
    }

    public String[] getAnnotationValues(AnnotationType aType) {    
        NodeIterable neighbors = getAnnotations(aType);
        Node[] nodes = neighbors.toArray();
        String[] values = new String[nodes.length];
        int pos = 0;
        for (Node node : nodes) {
            values[pos++] = node.getLabel();
        }
        return values;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 97 * hash + (int) (this.neoId ^ (this.neoId >>> 32));
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final NeoPeptide other = (NeoPeptide) obj;
        return other.neoId == this.neoId;
    }


}
