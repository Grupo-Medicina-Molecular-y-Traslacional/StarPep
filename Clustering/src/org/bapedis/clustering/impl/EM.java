/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author loge
 */
public class EM extends WekaClusterer<weka.clusterers.EM> {
    
    public EM(AlgorithmFactory factory) {
        super(new weka.clusterers.EM(), factory);
    }

    @Override
    protected void cluterize() {
        
    }
    
}
