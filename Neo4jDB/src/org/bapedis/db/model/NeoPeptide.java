/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import org.bapedis.core.model.AnnotationType;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.gephi.graph.api.EdgeIterable;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphView;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NeoPeptide extends Peptide {

    protected final long neoId;

    public static String getPrefixName() {
        return NbBundle.getMessage(NeoPeptide.class, "NeoPeptide.prefix");
    }

    public NeoPeptide(long neoId, Node graphNode, Graph graph) {
        super(graphNode, graph);
        this.neoId = neoId;
    }

    public long getNeoId() {
        return neoId;
    }



    public NodeIterable getNeighbors(AnnotationType aType) {
        int relType = graph.getModel().getEdgeType(aType.getRelationType());
        return relType != -1 ? graph.getNeighbors(graphNode, relType): new NodeIterable.NodeIterableEmpty();
    }
   

    public String[] getAnnotationValues(AnnotationType aType) {
        NodeIterable neighbors = getNeighbors(aType);
        ArrayList<String> values = new ArrayList<>();
        for(Node node: neighbors){
            values.add(node.getLabel());
        }
        return values.toArray(new String[0]);
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
