/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import weka.clusterers.SimpleKMeans;

/**
 *
 * @author loge
 */
public class KMeans extends WekaClusterer<SimpleKMeans> {
    
    public KMeans(AlgorithmFactory factory) {
        super(new SimpleKMeans(), factory);
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }        
    
}
