/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class)
public class SequenceSimilarityNetworkFactory implements AlgorithmFactory {

    private final SequenceSimilarityNetworkPanel panel = new SequenceSimilarityNetworkPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.SimilarityNetwork;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SequenceSimilarityNetwork.class, "SequenceSimilarityNetwork.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(SequenceSimilarityNetwork.class, "SequenceSimilarityNetwork.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return panel;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new SequenceSimilarityNetwork(this);
    }

    @Override
    public int getQualityRank() {
        return 5;
    }

    @Override
    public int getSpeedRank() {
        return 3;
    }
    
}
