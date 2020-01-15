/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.NetworkTag;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 250)
public class BiPartitionFactory implements NetworkTag{

    BiPartitionSetupUI setupUI = new BiPartitionSetupUI();
    
    @Override
    public String getCategory() {
        return NbBundle.getMessage(BiPartitionFactory.class, "BiPartition.category");
    }

    @Override
    public String getName() {
         return NbBundle.getMessage(BiPartitionFactory.class, "BiPartition.name");
    }

    @Override
    public String getDescription() {
         return NbBundle.getMessage(BiPartitionFactory.class, "BiPartition.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new BiPartition(this);
    }
    
}
