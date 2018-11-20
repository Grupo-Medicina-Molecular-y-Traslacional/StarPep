/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.SequenceTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 20)
public class NonRedundantSetAlgFactory implements SequenceTag {

    private final NonRedundantSetAlgSetupUI setupUI = new NonRedundantSetAlgSetupUI();
    
    
    @Override
    public String getName() {
        return NbBundle.getMessage(NonRedundantSetAlg.class, "NonRedundantSetAlg.name");
    }

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(NonRedundantSetAlg.class, "NonRedundantSetAlg.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new NonRedundantSetAlg(this);
    }

    
}
