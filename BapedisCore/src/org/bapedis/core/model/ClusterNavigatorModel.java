/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.Collections;
import java.util.List;

/**
 *
 * @author loge
 */
public class ClusterNavigatorModel {
    protected final List<Cluster> clusterList;

    public ClusterNavigatorModel(List<Cluster> clusterList) {
        this.clusterList = clusterList;
    }

    public List<Cluster> getClusterList() {
        return Collections.unmodifiableList(clusterList);
    }
        
}
