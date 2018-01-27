/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import java.util.List;
import org.bapedis.core.model.Peptide;
import org.bapedis.network.model.Cluster;
import org.bapedis.network.model.SeqClusteringModel;

/**
 *
 * @author loge
 */
public class SeqClusterBuilder {
    protected final Peptide[] peptide;

    public SeqClusterBuilder(Peptide[] peptide) {
        this.peptide = peptide;
    }
    
    public List<Cluster> clusterize(SeqClusteringModel model){
    
        return null;
    }
}
