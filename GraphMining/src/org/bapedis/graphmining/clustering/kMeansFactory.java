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
@ServiceProvider(service = AlgorithmFactory.class, position = 240)
public class kMeansFactory implements NetworkTag{
    
    @Override
    public String getCategory() {
        return NbBundle.getMessage(KMeans.class, "KMeans.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(KMeans.class, "KMeans.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(KMeans.class, "KMeans.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new KMeans(this);
    }
    
}
