/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.FeatureSubsetOptimizationTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = FeatureSubsetOptimizationTag.class, position = 20)
public class FeatureSubsetOptimizationFactory implements FeatureSubsetOptimizationTag {

    private final FeatureSubsetOptimizationSetupUI setupUI = new FeatureSubsetOptimizationSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(FeatureSubsetOptimizationFactory.class, "FeatureSubsetOptimization.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(FeatureSubsetOptimizationFactory.class, "FeatureSubsetOptimization.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new FeatureSubsetOptimization(this);
    }
    
}
