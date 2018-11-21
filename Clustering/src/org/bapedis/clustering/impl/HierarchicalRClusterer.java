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
public class HierarchicalRClusterer extends RClusterer {

    public HierarchicalRClusterer(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    protected String getRScriptCode() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
