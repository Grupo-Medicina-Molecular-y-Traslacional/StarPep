/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.FeatureSelectionTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 0)
public class UnsupervisedFeatureSelectionFactory implements AlgorithmFactory, FeatureSelectionTag {

    UnsupervisedFeatureSelectionSetupUI setupUI = new UnsupervisedFeatureSelectionSetupUI();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(UnsupervisedFeatureSelection.class, "UnsupervisedFeatureSelection.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(UnsupervisedFeatureSelection.class, "UnsupervisedFeatureSelection.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new UnsupervisedFeatureSelection(this);
    }
    
}
