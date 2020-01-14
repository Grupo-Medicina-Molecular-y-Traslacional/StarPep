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
public class AngularSeparation extends AbstractDistance {

    public AngularSeparation(AlgorithmFactory factory) {
        super(factory);
    }

    @Override
    double compute() {
        double val1, val2;
        double num = 0;
        double den1 = 0, den2 = 0;
        for (int j=0; j < descriptorMatrix[index1].length; j++) {
            val1 = descriptorMatrix[index1][j];
            val2 = descriptorMatrix[index2][j];
            
            num += val1 * val2;
            den1 += val1 * val1;
            den2 += val2 * val2;
        }
        double sc = den1 == 0 || den2 == 0 ? 0 : (num / Math.sqrt(den1 * den2));
        sc = Math.round(sc * 100.0);
        sc /= 100.0;        
        assert sc >= 0 && sc <= 1: "sc: " + sc;
        return 1 - sc;
    }

}
