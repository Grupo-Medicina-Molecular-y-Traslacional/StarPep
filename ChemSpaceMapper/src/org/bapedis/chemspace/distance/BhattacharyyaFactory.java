/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author Loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 60)
public class BhattacharyyaFactory implements DistanceFunctionTag {

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return "Bhattacharyya";
    }

    @Override
    public String getDescription() {
        return "Bhattacharyya";
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new Bhattacharyya(this);
    }
    
}
