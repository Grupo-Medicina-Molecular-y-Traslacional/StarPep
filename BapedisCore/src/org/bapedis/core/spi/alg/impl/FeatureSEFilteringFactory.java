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
 * @author loge
 */
public class FeatureSEFilteringFactory implements AlgorithmFactory {

    private final FeatureSEFilteringPanel panel = new FeatureSEFilteringPanel();;
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(FeatureSEFiltering.class, "FeatureSEFiltering.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return panel;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new FeatureSEFiltering(this);
    }
    
}
