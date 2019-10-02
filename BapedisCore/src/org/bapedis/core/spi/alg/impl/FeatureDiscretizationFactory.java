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
public class FeatureDiscretizationFactory implements AlgorithmFactory {
   
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
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new FeatureDiscretization(this);
    }
    
}
