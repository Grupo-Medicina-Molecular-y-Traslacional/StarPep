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
@ServiceProvider(service = AlgorithmFactory.class, position = 10)
public class SequenceSearchFactory implements AlgorithmFactory{

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.Sequence;
    }

    @Override
    public String getName() {
       return NbBundle.getMessage(SequenceSearch.class, "SequenceSearch.name");
    }

    @Override
    public String getDescription() {
       return NbBundle.getMessage(SequenceSearch.class, "SequenceSearch.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new SequenceSearch(this);
    }

    @Override
    public int getQualityRank() {
        return -1;
    }

    @Override
    public int getSpeedRank() {
        return -1;
    }
    
}