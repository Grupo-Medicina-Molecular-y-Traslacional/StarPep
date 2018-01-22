/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.network.impl;

import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.network.SimilarityNetworkFactory;
import org.bapedis.core.spi.network.SimilarityNetworkSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = SimilarityNetworkFactory.class)
public class CSNFactory implements SimilarityNetworkFactory{

    private final CSNSetupUI setupUI = new CSNSetupUI();
            
    @Override
    public String getName() {
        return NbBundle.getMessage(CSNFactory.class, "CSNFactory.name");
    }


    @Override
    public SimilarityNetworkSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new CSNAlgorithm();
    }
    
}
