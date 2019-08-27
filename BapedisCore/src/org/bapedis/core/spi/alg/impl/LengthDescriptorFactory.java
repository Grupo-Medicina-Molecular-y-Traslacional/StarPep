/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.FeatureExtractionTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 0)
public class LengthDescriptorFactory implements FeatureExtractionTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(LengthDescriptor.class, "LengthDescriptor.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(LengthDescriptor.class, "LengthDescriptor.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(LengthDescriptor.class, "LengthDescriptor.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new LengthDescriptor(this);
    }
    
}
