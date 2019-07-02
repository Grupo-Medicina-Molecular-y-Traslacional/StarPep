/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.bapedis.core.spi.alg.FeatureExtractionTag;

/**
 *
 * @author beltran, loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 1000)
public class AACompositionFactory implements AlgorithmFactory, FeatureExtractionTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(AACompositionFactory.class, "AAComposition.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AACompositionFactory.class, "AAComposition.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AACompositionFactory.class, "AAComposition.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new AAComposition(this);
    }
    
}
