/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.modamp.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author beltran, loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 600)
public class AverageHydrophilicityFactory implements AlgorithmFactory {

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.MolecularDescriptor;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AverageHydrophilicityFactory.class, "AverageHydrophilicity.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AverageHydrophilicityFactory.class, "AverageHydrophilicity.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
       return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new AverageHydrophilicity(this);
    }

    @Override
    public int getQualityRank() {
        return -1;
    }

    @Override
    public int getSpeedRank() {
        return -1;
    }
    
}