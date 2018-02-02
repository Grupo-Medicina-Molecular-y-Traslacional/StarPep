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

/**
 *
 * @author loge
 */
public class RemoveDescriptorFactory implements AlgorithmFactory{

    @Override
    public AlgorithmCategory getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(RemoveDescriptorAlgo.class, "RemoveDescriptor.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(RemoveDescriptorAlgo.class, "RemoveDescriptor.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new RemoveDescriptorAlgo(this);
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
