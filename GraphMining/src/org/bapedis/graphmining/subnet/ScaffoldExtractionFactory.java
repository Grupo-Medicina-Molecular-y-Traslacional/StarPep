/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.subnet;

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
@ServiceProvider(service=AlgorithmFactory.class, position = 400)
public class ScaffoldExtractionFactory implements NetworkTag {
    ScaffoldExtractionSetupUI setupUI = new ScaffoldExtractionSetupUI();

    @Override
    public String getCategory() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(ScaffoldExtraction.class, "ScaffoldExtraction.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new ScaffoldExtraction(this);
    }
    
}
