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
 * @author beltran
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 800)
public class AliphaticIndexFactory implements AlgorithmFactory {

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.MolecularDescriptor;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AliphaticIndexFactory.class, "AliphaticIndex.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AliphaticIndexFactory.class, "AliphaticIndex.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new AliphaticIndex(this);
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
