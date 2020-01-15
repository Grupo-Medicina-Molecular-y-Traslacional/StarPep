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
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 250)
public class KMeansWrapperFactory implements NetworkTag{

    @Override
    public String getCategory() {
        return NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(KMeansWrapper.class, "KMeansWrapper.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new KMeansWrapper(this);
    }
    
}
