/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.MolecularDescriptorTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author beltran, loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 700)
public class HydrophobicPeriodicityFactory implements AlgorithmFactory, MolecularDescriptorTag{

    @Override
    public String getCategory() {
        return NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(HydrophobicPeriodicityFactory.class, "HydrophobicPeriodicity.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new HydrophobicPeriodicity(this);
    }
    
}
