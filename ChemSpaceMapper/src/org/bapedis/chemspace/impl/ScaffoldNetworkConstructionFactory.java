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
@ServiceProvider(service = AlgorithmFactory.class, position = 300)
public class ScaffoldNetworkConstructionFactory implements NetworkConstructionTag {
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ScaffoldNetworkConstructionFactory.class, "ScadffoldNetworkConstruction.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ScaffoldNetworkConstructionFactory.class, "ScadffoldNetworkConstruction.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return  new ScaffoldNetworkConstructionSetupUI();
    }

    @Override
    public Algorithm createAlgorithm() {
        return new ScaffoldNetworkConstruction(this);
    }    
    
}
