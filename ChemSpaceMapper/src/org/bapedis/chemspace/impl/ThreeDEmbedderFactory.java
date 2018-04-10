/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class ThreeDEmbedderFactory implements AlgorithmFactory{

    @Override
    public AlgorithmCategory getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ThreeDEmbedderFactory.class, "ThreeDEmbedder.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ThreeDEmbedderFactory.class, "ThreeDEmbedder.desc");
    }

    @Override
    public Algorithm createAlgorithm() {
        return new ThreeDEmbedder(this);
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }
    
}
