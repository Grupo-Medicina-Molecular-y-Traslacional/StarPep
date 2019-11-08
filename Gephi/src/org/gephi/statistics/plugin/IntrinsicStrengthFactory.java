/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.NetworkTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service=AlgorithmFactory.class)
public class IntrinsicStrengthFactory implements NetworkTag {

    @Override
    public String getCategory() {
        return NbBundle.getMessage(HarmonicCentralityFactory.class, "IntrinsicStrength.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(IntrinsicStrength.class, "IntrinsicStrength.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(IntrinsicStrength.class, "IntrinsicStrength.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new IntrinsicStrength(this);
    }
    
}
