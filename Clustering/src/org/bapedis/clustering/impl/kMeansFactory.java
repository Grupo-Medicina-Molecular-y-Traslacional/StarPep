/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ClusteringTag;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import weka.clusterers.SimpleKMeans;

/**
 *
 * @author loge
 */

public class kMeansFactory implements ClusteringTag{
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(KMeans.class, "KMeans.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(KMeans.class, "KMeans.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new KMeans(this);
    }
    
}
