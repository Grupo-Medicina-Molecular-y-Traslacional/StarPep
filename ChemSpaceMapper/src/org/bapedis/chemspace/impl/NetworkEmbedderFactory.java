/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NetworkEmbedderFactory extends AbstractEmbedderFactory{

    @Override
    public AlgorithmCategory getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(NetworkEmbedderFactory.class, "NetworkEmbedder.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(NetworkEmbedderFactory.class, "NetworkEmbedder.desc");
    }

    @Override
    public Algorithm createAlgorithm() {
        return new NetworkEmbedder();
    }
    
}
