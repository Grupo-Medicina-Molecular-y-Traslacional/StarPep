/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

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
@ServiceProvider(service = AlgorithmFactory.class, position = 20)
public class SequenceClusteringFactory implements AlgorithmFactory {

    private final SequenceClusteringPanel setupUI = new SequenceClusteringPanel();
    
    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.Sequence;
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
