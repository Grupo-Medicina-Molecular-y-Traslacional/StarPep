/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.model;

import java.util.Comparator;
import org.bapedis.core.model.Peptide;
import org.gephi.graph.api.Node;

/**
 *
 * @author Loge
 */
public class PeptideRankComparator implements Comparator<Peptide> {

    private final String attribute;

    public PeptideRankComparator(String attribute) {
        this.attribute = attribute;
    }

    @Override
    public int compare(Peptide peptide1, Peptide peptide2) {
        Node node1 = peptide1.getGraphNode();
        Node node2 = peptide2.getGraphNode();
        Double val1 = (Double) node1.getAttribute(attribute);
        Double val2 = (Double) node2.getAttribute(attribute);
        return val2.compareTo(val1);
    }

}
