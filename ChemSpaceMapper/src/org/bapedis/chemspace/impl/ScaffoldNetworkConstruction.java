/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author Loge
 */
public class ScaffoldNetworkConstruction extends NetworkConstructionAlg implements Cloneable {
    protected double diversityRadio;

    public ScaffoldNetworkConstruction(AlgorithmFactory factory) {
        super(factory);
         diversityRadio = 0.8;
    }
            
    public double getDiversityRadio() {
        return diversityRadio;
    }

    public void setDiversityRadio(double diversityRadio) {
        this.diversityRadio = diversityRadio;
    }         

    @Override
    protected double createNetwork() {
        pc.reportMsg("Diversity radio: " + String.format("%.2f", diversityRadio), workspace);
        return 0;
    }
    
    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }      
}
