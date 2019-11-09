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
@ServiceProvider(service = AlgorithmFactory.class, position = 100)
public class CSNetworkConstructionFactory implements NetworkConstructionTag {
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(CSNetworkConstructionFactory.class, "CSNetworkConstruction.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(CSNetworkConstructionFactory.class, "CSNetworkConstruction.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return  null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new CSNetworkConstruction(this);
    }    
}
