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
public class ChemicalSpaceNetworkFactory implements AlgorithmFactory {

    private final ChemicalSpaceNetworkPanel panel = new ChemicalSpaceNetworkPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.SimilarityNetwork;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ChemicalSpaceNetworkFactory.class, "ChemicalSpaceNetworkFactory.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ChemicalSpaceNetworkFactory.class, "ChemicalSpaceNetworkFactory.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return panel;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new ChemicalSpaceNetwork(this);
    }

    @Override
    public int getQualityRank() {
        return 4;
    }

    @Override
    public int getSpeedRank() {
        return 4;
    }
    
}
