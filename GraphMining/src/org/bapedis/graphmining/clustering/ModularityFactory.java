/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.NetworkTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 210)
public class ModularityFactory implements NetworkTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(ModularityFactory.class, "ModularityFactory.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ModularityFactory.class, "ModularityFactory.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ModularityFactory.class, "ModularityFactory.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new Modularity(this);
    }
    
}
