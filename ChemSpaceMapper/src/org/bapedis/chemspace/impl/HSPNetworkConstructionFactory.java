/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 200)
public class HSPNetworkConstructionFactory implements NetworkConstructionTag {
    private HSPNetworkConstructionSetupUI setupUI = new HSPNetworkConstructionSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HSPNetworkConstructionFactory.class, "HSPNetworkConstruction.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(HSPNetworkConstructionFactory.class, "HSPNetworkConstruction.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return  setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new HSPNetworkConstruction(this);
    }        
}
