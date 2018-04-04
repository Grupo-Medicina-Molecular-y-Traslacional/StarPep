/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;

/**
 *
 * @author loge
 */
public abstract class AbstractEmbedderFactory implements AlgorithmFactory{

    private final AbstractEmbedderPanel setupUI = new AbstractEmbedderPanel();
    
    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }    
    
}
