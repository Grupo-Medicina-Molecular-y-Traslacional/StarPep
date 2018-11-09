/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class ClusterNode extends AbstractNode{
    private final Cluster cluster;     
    
    public ClusterNode(Cluster cluster) {
        super(Children.LEAF, Lookups.singleton(cluster));
        this.cluster = cluster;
    }

    @Override
    public String getDisplayName() {
        return "Cluster " + cluster.getId(); 
    }
    
    
    
}
