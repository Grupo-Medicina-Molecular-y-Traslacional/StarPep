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
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.alg.FeatureExtractionTag;

/**
 *
 * @author beltran, loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 1500)
public class TripeptideCompositionFactory implements AlgorithmFactory, FeatureExtractionTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TripeptideCompositionFactory.class, "TripeptideComposition.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new TripeptideComposition(this);
    }
    
}
