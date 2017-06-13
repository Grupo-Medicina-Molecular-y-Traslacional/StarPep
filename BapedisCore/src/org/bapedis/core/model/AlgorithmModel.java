/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.bapedis.core.spi.algo.Algorithm;

/**
 *
 * @author loge
 */
public class AlgorithmModel {
    protected Class algoClassFactory;
    protected Algorithm selectedAlgorithm;
    

    public Class getAlgoClassFactory() {
        return algoClassFactory;
    }

    public void setAlgoClassFactory(Class algoClassFactory) {
        this.algoClassFactory = algoClassFactory;
    }

    public Algorithm getSelectedAlgorithm() {
        return selectedAlgorithm;
    }

    public void setSelectedAlgorithm(Algorithm selectedAlgorithm) {
        this.selectedAlgorithm = selectedAlgorithm;
    }        
    
}
