/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.model;

import java.util.Comparator;
import org.gephi.graph.api.Node;

/**
 *
 * @author Loge
 */
public class NodeCentralityComparator implements Comparator<Node> {
    private final String attribute;

    public NodeCentralityComparator(String attribute) {
        this.attribute = attribute;
    }        

    @Override
    public int compare(Node o1, Node o2) {
        Double val1 = (Double)o1.getAttribute(attribute);
        Double val2 = (Double)o2.getAttribute(attribute); 
        return val2.compareTo(val1);
    }
    
}
