/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

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
@ServiceProvider(service = AlgorithmFactory.class, position = 105)
public class SequenceClusteringFactory implements NetworkTag {

    private final SequenceClusteringPanel setupUI = new SequenceClusteringPanel();
    
    @Override
    public String getCategory() {
        return NbBundle.getMessage(SequenceClustering.class, "SequenceClustering.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(SequenceClustering.class, "SequenceClustering.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(SequenceClustering.class, "SequenceClustering.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new SequenceClustering(this);
    }
    
}
