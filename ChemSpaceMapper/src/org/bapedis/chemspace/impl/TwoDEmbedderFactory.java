/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class TwoDEmbedderFactory implements AlgorithmFactory{

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(TwoDEmbedderFactory.class, "TwoDEmbedder.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(TwoDEmbedderFactory.class, "TwoDEmbedder.desc");
    }

    @Override
    public Algorithm createAlgorithm() {
        return null;
//        return new TwoDEmbedder(this);
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }
    
}
