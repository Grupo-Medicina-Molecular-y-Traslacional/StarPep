/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.NetworkTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service=AlgorithmFactory.class)
public class BetweenessCentralityFactory implements NetworkTag {

    @Override
    public String getName() {
        return NbBundle.getMessage(BetweenessCentralityFactory.class, "BetweenessCentrality.name");
    }

    @Override
    public String getCategory() {
        return NbBundle.getMessage(BetweenessCentralityFactory.class, "BetweenessCentrality.category");
    }

    @Override
    public String getDescription() {
       return NbBundle.getMessage(BetweenessCentralityFactory.class, "BetweenessCentrality.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new BetweenessCentrality(this);
    }
}
