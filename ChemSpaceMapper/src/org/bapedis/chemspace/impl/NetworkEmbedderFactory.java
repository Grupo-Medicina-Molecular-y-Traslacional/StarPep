/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class NetworkEmbedderFactory implements AlgorithmFactory {

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(NetworkEmbedderFactory.class, "SSNEmbedder.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(NetworkEmbedderFactory.class, "SSNEmbedder.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return  null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new NetworkEmbedderAlg(this);
    }
    
}
