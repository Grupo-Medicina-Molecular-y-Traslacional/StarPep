/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.featureSelection.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.FeatureDiscretizationTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = FeatureDiscretizationTag.class, position = 0)
public class FeatureDiscretizationFactory implements FeatureDiscretizationTag {

    FeatureDiscretizationPanel setupUI = new FeatureDiscretizationPanel();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(FeatureDiscretizationFactory.class, "FeatureDiscretization.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(FeatureDiscretizationFactory.class, "FeatureDiscretization.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new FeatureDiscretization(this);
    }
    
}
