/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.distance;

import org.bapedis.core.spi.alg.AlgorithmFactory;

/**
 *
 * @author Loge
 */

public class Euclidean extends AbstractDistance {

    public Euclidean(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    public double compute() {
        double val1, val2, d;
        double sum = 0;
        for (int j=0; j < descriptorMatrix[index1].length; j++) {
            val1 = descriptorMatrix[index1][j];
            val2 = descriptorMatrix[index2][j];
            
            d = val1-val2;
            sum += d*d;
        }
        return Math.sqrt(sum);
    }
    
}
