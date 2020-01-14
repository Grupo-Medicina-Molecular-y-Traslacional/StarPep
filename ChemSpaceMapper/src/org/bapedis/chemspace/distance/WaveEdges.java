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
public class WaveEdges extends AbstractDistance {

    public WaveEdges(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    double compute() {
        double val1, val2;
        double sum = 0;
        for (int j=0; j < descriptorMatrix[index1].length; j++) {
            val1 = descriptorMatrix[index1][j];
            val2 = descriptorMatrix[index2][j];
            
            sum += (1 - Math.min(val1, val2)/Math.max(val1, val2));
        }
        return sum;
    }

}
