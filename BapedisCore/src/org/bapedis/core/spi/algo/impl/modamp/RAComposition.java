/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl.modamp;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.spi.algo.AlgorithmFactory;

/**
 *
 * @author loge
 */
public class RAComposition extends AbstractModamp {

    public RAComposition(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public void compute(Peptide peptide) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
