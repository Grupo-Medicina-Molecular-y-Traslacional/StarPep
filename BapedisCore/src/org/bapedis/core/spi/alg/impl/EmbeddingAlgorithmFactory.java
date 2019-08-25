/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class EmbeddingAlgorithmFactory implements AlgorithmFactory {

    private static EmbeddingAlgorithmPanel setupUI = new EmbeddingAlgorithmPanel();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(EmbeddingAlgorithm.class, "EmbeddingAlgorithm.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new EmbeddingAlgorithm(this);
    }
    
}
