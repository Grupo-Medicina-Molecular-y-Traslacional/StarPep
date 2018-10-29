/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ClusteringTag;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class EMFactory implements ClusteringTag {

    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
         return NbBundle.getMessage(EM.class, "EM.name");
    }

    @Override
    public String getDescription() {
         return NbBundle.getMessage(EM.class, "EM.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new EM(this);
    }
    
}
